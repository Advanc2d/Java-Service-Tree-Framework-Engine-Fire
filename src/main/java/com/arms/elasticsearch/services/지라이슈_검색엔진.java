package com.arms.elasticsearch.services;

import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.repositories.ProductRepository;
import com.arms.elasticsearch.repositories.지라이슈_저장소;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

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
    public 지라이슈 인덱스_추가하기(지라이슈 지라이슈) {


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
}

