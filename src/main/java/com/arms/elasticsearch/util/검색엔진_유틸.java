package com.arms.elasticsearch.util;
import com.arms.elasticsearch.helper.인덱스자료;
import com.arms.elasticsearch.models.지라이슈;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public final class 검색엔진_유틸 {

    private final RestHighLevelClient client;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public 검색엔진_유틸(RestHighLevelClient client) {
        this.client = client;
    }

    public <T> List<T>  searchInternal(final SearchRequest request,Class<T> valueType) {
        if (request == null) {
            log.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<T> vehicles = new ArrayList<>(searchHits.length);

            for (SearchHit hit : searchHits) {

                vehicles.add(
                        MAPPER.readValue(hit.getSourceAsString(), valueType)
                );
            }

            return vehicles;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    public <T> List<T>  getAllCreatedSince(final Date date,Class<T> valueType) {
        final SearchRequest request = this.buildSearchRequest(
                인덱스자료.지라이슈_인덱스명,
                "created",
                date
        );

        return searchInternal(request,valueType);
    }

    public <T> List<T>  searchCreatedSince(final SearchDTO dto, final Date date,Class<T> valueType) {
        final SearchRequest request = 검색엔진_유틸.buildSearchRequest(
                인덱스자료.지라이슈_인덱스명,
                dto,
                date
        );

        return searchInternal(request,valueType);
    }

    public Boolean index(final 지라이슈 지라_이슈) {
        try {
            final String vehicleAsString = MAPPER.writeValueAsString(지라_이슈);

            final IndexRequest request = new IndexRequest(인덱스자료.지라이슈_인덱스명);
            request.id(지라_이슈.getId());
            request.source(vehicleAsString, XContentType.JSON);

            final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            return response != null && response.status().equals(RestStatus.OK);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public <T> T getById(final String 이슈_아이디,Class<T> valueType) {
        try {
            final GetResponse documentFields = client.get(
                    new GetRequest(인덱스자료.지라이슈_인덱스명, 이슈_아이디),
                    RequestOptions.DEFAULT
            );
            if (documentFields == null || documentFields.isSourceEmpty()) {
                return null;
            }

            return MAPPER.readValue(documentFields.getSourceAsString(), valueType);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final SearchDTO dto) {
        try {
            final int page = dto.getPage();
            final int size = dto.getSize();
            final int from = page <= 0 ? 0 : page * size;

            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .from(from)
                    .size(size)
                    .postFilter(getQueryBuilder(dto));

            if (dto.getSortBy() != null) {
                builder = builder.sort(
                        dto.getSortBy(),
                        dto.getOrder() != null ? dto.getOrder() : SortOrder.ASC
                );
            }

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final String field,
                                                   final Date date) {
        try {
            final SearchSourceBuilder builder = new SearchSourceBuilder()
                    .postFilter(getQueryBuilder(field, date));

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final SearchDTO dto,
                                                   final Date date) {
        try {
            final QueryBuilder searchQuery = getQueryBuilder(dto);
            final QueryBuilder dateQuery = getQueryBuilder("created", date);

            final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .mustNot(searchQuery)
                    .must(dateQuery);

            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .postFilter(boolQuery);

            if (dto.getSortBy() != null) {
                builder = builder.sort(
                        dto.getSortBy(),
                        dto.getOrder() != null ? dto.getOrder() : SortOrder.ASC
                );
            }

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static QueryBuilder getQueryBuilder(final SearchDTO dto) {
        if (dto == null) {
            return null;
        }

        final List<String> fields = dto.getFields();
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }

        if (fields.size() > 1) {
            final MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(dto.getSearchTerm())
                    .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                    .operator(Operator.AND);

            fields.forEach(queryBuilder::field);

            return queryBuilder;
        }

        return fields.stream()
                .findFirst()
                .map(field ->
                        QueryBuilders.matchQuery(field, dto.getSearchTerm())
                                .operator(Operator.AND))
                .orElse(null);
    }

    private static QueryBuilder getQueryBuilder(final String field, final Date date) {
        return QueryBuilders.rangeQuery(field).gte(date);
    }

}
