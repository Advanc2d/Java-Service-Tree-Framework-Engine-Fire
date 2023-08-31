package com.arms.jira.jiraissuetype.strategy;

import com.arms.errors.codes.에러코드;
import com.arms.jira.jiraissuetype.model.지라이슈유형_데이터;
import com.arms.jira.utils.지라유틸;
import com.arms.jira.info.model.지라연결정보_데이터;
import com.arms.jira.info.service.지라연결_서비스;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class 클라우드_지라이슈유형_전략 implements 지라이슈유형_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @Override
    public List<지라이슈유형_데이터> 이슈_유형_목록_가져오기(Long 연결_아이디) {
        로그.info("클라우드 지라 이슈_유형_목록_가져오기");
        try {
            String endpoint = "/rest/api/3/issuetype";

            지라연결정보_데이터 found = 지라연결_서비스.checkInfo(연결_아이디);
            WebClient webClient = 지라유틸.클라우드_통신기_생성(found.getUri(), found.getUserId(), found.getPasswordOrToken());

            List<지라이슈유형_데이터> 반환할_이슈_유형_목록
                                        = 지라유틸.get(webClient, endpoint,
                                        new ParameterizedTypeReference<List<지라이슈유형_데이터>>() {}).block();

            로그.info(반환할_이슈_유형_목록.toString());

            return 반환할_이슈_유형_목록;
        }catch (Exception e){
            로그.error("클라우드 지라 이슈 유형 목록 가져오기 가져오기에 실패하였습니다." + e.getMessage());
            throw new IllegalArgumentException(에러코드.이슈유형_조회_오류.getErrorMsg());
        }
    }

    @Override
    public List<지라이슈유형_데이터> 프로젝트별_이슈_유형_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) {

        로그.info("클라우드 지라 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈_유형_목록_가져오기");

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.검색정보_오류.getErrorMsg());
        }

        try {
            String endpoint = "/rest/api/3/issuetype/project?projectId=" + 프로젝트_아이디;

            지라연결정보_데이터 found = 지라연결_서비스.checkInfo(연결_아이디);
            WebClient webClient = 지라유틸.클라우드_통신기_생성(found.getUri(), found.getUserId(), found.getPasswordOrToken());

            List<지라이슈유형_데이터> 반환할_이슈_유형_목록
                    = 지라유틸.get(webClient, endpoint,
                            new ParameterizedTypeReference<List<지라이슈유형_데이터>>() {}).block();

            로그.info(반환할_이슈_유형_목록.toString());

            return 반환할_이슈_유형_목록;
        }catch (Exception e){
            로그.error("클라우드 지라 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈_유형_목록_가져오기에 실패하였습니다.");
            throw new IllegalArgumentException(에러코드.이슈유형_조회_오류.getErrorMsg());
        }
    }
}
