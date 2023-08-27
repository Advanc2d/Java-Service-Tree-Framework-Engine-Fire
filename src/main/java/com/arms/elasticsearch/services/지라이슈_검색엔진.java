package com.arms.elasticsearch.services;

import com.arms.elasticsearch.helper.인덱스자료;
import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.repositories.지라이슈_저장소;
import com.arms.elasticsearch.util.SearchDTO;
import com.arms.elasticsearch.util.검색유틸;
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
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("지라이슈_서비스")
public class 지라이슈_검색엔진 implements 지라이슈_서비스{

    private 지라이슈_저장소 지라이슈저장소;

    private 검색유틸 검색유틸;


    @Autowired
    public 지라이슈_검색엔진(final 지라이슈_저장소 지라이슈저장소, final 검색유틸 검색유틸) {
        super();
        this.지라이슈저장소 = 지라이슈저장소;
        this.검색유틸 = 검색유틸;
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

        return 검색유틸.searchInternal(request);
    }



}

