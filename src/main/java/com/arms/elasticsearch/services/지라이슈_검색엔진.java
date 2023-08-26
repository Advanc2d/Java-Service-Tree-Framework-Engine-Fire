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

import java.util.ArrayList;
import java.util.Collections;
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


        if (지라이슈저장소 == null) {

            log.info("check");
        }else{

            if( 지라이슈 == null ){
                log.info("fire");
            }

        }

        지라이슈 결과 = 지라이슈저장소.save(지라이슈);

        return 결과;
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


}

