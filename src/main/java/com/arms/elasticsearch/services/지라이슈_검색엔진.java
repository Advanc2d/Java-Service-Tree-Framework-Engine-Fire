package com.arms.elasticsearch.services;

import com.arms.elasticsearch.helper.인덱스자료;
import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.repositories.지라이슈_저장소;
import com.arms.elasticsearch.util.SearchDTO;
import com.arms.elasticsearch.util.검색유틸;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.xcontent.XContentType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("지라이슈_서비스")
public class 지라이슈_검색엔진 implements 지라이슈_서비스{

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private 지라이슈_저장소 지라이슈저장소;

    private final RestHighLevelClient client;

    @Autowired
    public 지라이슈_검색엔진(final 지라이슈_저장소 지라이슈저장소, RestHighLevelClient client) {
        super();
        this.지라이슈저장소 = 지라이슈저장소;
        this.client = client;
    }


    @Override
    public 지라이슈 이슈_추가하기(지라이슈 지라이슈) {

        지라이슈 결과 = 지라이슈저장소.save(지라이슈);
        return 결과;
    }

    @Override
    public 지라이슈 이슈_갱신하기(지라이슈 지라_이슈) {

        지라이슈 이슈 = 이슈_조회하기(지라_이슈.getId());

        지라이슈 결과 = 지라이슈저장소.save(이슈);
        return 결과;
    }

    @Override
    public 지라이슈 이슈_삭제하기(지라이슈 지라_이슈) {

        지라이슈 이슈 = 이슈_조회하기(지라_이슈.getId());
        log.info("왠만하면 쓰지 마시지...");

        if( 이슈 == null ){
            return null;
        }else{
            지라이슈저장소.delete(이슈);
            return 이슈;
        }
    }

    @Override
    public 지라이슈 이슈_조회하기(String 조회조건_아이디) {
        return 지라이슈저장소.findById(조회조건_아이디).orElse(null);
    }

    @Override
    public List<지라이슈> 이슈_검색하기(SearchDTO 검색조건) {
        final SearchRequest request = 검색유틸.buildSearchRequest(
                인덱스자료.지라이슈_인덱스명,
                검색조건
        );

        return searchInternal(request);
    }

    private List<지라이슈> searchInternal(final SearchRequest request) {
        if (request == null) {
            log.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<지라이슈> vehicles = new ArrayList<>(searchHits.length);

            for (SearchHit hit : searchHits) {

                vehicles.add(
                        MAPPER.readValue(hit.getSourceAsString(), 지라이슈.class)
                );
            }

            return vehicles;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<지라이슈> getAllCreatedSince(final Date date) {
        final SearchRequest request = 검색유틸.buildSearchRequest(
                인덱스자료.지라이슈_인덱스명,
                "created",
                date
        );

        return searchInternal(request);
    }

    public List<지라이슈> searchCreatedSince(final SearchDTO dto, final Date date) {
        final SearchRequest request = 검색유틸.buildSearchRequest(
                인덱스자료.지라이슈_인덱스명,
                dto,
                date
        );

        return searchInternal(request);
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

    public 지라이슈 getById(final String 이슈_아이디) {
        try {
            final GetResponse documentFields = client.get(
                    new GetRequest(인덱스자료.지라이슈_인덱스명, 이슈_아이디),
                    RequestOptions.DEFAULT
            );
            if (documentFields == null || documentFields.isSourceEmpty()) {
                return null;
            }

            return MAPPER.readValue(documentFields.getSourceAsString(), 지라이슈.class);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


}

