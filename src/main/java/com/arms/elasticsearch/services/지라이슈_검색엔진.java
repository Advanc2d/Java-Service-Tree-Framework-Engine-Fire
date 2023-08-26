package com.arms.elasticsearch.services;

import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.repositories.지라이슈_저장소;
import com.arms.elasticsearch.util.SearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("지라이슈_서비스")
public class 지라이슈_검색엔진 implements 지라이슈_서비스{

    private 지라이슈_저장소 지라이슈저장소;

    @Autowired
    public 지라이슈_검색엔진(final 지라이슈_저장소 지라이슈저장소) {
        super();
        this.지라이슈저장소 = 지라이슈저장소;
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
        return null;
    }


}

