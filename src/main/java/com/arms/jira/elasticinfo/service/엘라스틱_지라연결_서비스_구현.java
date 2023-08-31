package com.arms.jira.elasticinfo.service;

import com.arms.jira.elasticinfo.model.엘라스틱_지라연결정보_데이터;
import com.arms.jira.elasticinfo.model.엘라스틱_지라연결정보_엔티티;
import com.arms.jira.elasticinfo.repositories.엘라스틱_지라연결_저장소;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("엘라스틱_지라연결_서비스")
@AllArgsConstructor
public class 엘라스틱_지라연결_서비스_구현 implements 엘라스틱_지라연결_서비스{
    @Autowired
    private 엘라스틱_지라연결_저장소 엘라스틱_지라연결_저장소;

    @Override
    public 엘라스틱_지라연결정보_엔티티 연결정보_저장(엘라스틱_지라연결정보_데이터 지라연결정보_데이터){

//        엘라스틱_지라연결정보_데이터 가져온_지라연결정보_데이터 = loadConnectInfo(지라연결정보_데이터.getConnectId());
//        엘라스틱_지라연결정보_엔티티 엘라스틱_지라연결정보_엔티티;
//
//
//
//        엘라스틱_지라연결정보_엔티티 결과 = 엘라스틱_지라연결_저장소.save(엘라스틱_지라연결정보_엔티티);
//
//        return 결과;
    }

}
