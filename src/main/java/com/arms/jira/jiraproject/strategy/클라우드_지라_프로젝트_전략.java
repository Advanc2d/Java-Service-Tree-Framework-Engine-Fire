package com.arms.jira.jiraproject.strategy;

import com.arms.jira.utils.지라유틸;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.jiraproject.model.지라_프로젝트_데이터_전송_객체;
import com.arms.jira.jiraproject.model.클라우드_프로젝트_전체_데이터_전송_객체;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class 클라우드_지라_프로젝트_전략 implements 지라_프로젝트_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @Autowired
    private 지라유틸 지라유틸;

    @Override
    public 지라_프로젝트_데이터_전송_객체 프로젝트_상세정보_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {
        로그.info("클라우드 지라 프로젝트 "+ 프로젝트_키_또는_아이디 +" 상세정보 가져오기");

        String endpoint = "/rest/api/3/project/"+ 프로젝트_키_또는_아이디;

        JiraInfoDTO found = 지라연결_서비스.checkInfo(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        지라_프로젝트_데이터_전송_객체 반환할_지라_프로젝트_상세정보 = 지라유틸.get(webClient, endpoint, 지라_프로젝트_데이터_전송_객체.class).block();

        로그.info(반환할_지라_프로젝트_상세정보.toString());

        return 반환할_지라_프로젝트_상세정보;
    }

    @Override
    public List<지라_프로젝트_데이터_전송_객체> 프로젝트_전체_목록_가져오기(Long 연결_아이디) {
        로그.info("클라우드 지라 프로젝트 전체 목록 가져오기");

        JiraInfoDTO found = 지라연결_서비스.checkInfo(연결_아이디);

        if (found == null) {
            // throw Exception e; ControllerAdvice 오류 처리
        }

        WebClient webClient = 지라유틸.클라우드_통신기_생성(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        int startAt = 0;
        int 최대_검색수 = 지라유틸.최대_검색수_가져오기();
        boolean isLast = false;

        List<지라_프로젝트_데이터_전송_객체> 반환할_프로젝트_데이터전송객체_목록 = new ArrayList<>();

        while(!isLast) {
            String endpoint = "/rest/api/3/project/search?maxResults="+ 최대_검색수 + "&startAt=" + startAt;
            클라우드_프로젝트_전체_데이터_전송_객체 클라우드_프로젝트_전체_데이터_전송_객체
                                    = 지라유틸.get(webClient, endpoint, 클라우드_프로젝트_전체_데이터_전송_객체.class).block();

            반환할_프로젝트_데이터전송객체_목록.addAll(클라우드_프로젝트_전체_데이터_전송_객체.getValues());

            if (클라우드_프로젝트_전체_데이터_전송_객체.getTotal() == 반환할_프로젝트_데이터전송객체_목록.size()) {
                isLast = true;
            }
            else {
                startAt += 최대_검색수;
            }
        }

        로그.info(반환할_프로젝트_데이터전송객체_목록.toString());

        return 반환할_프로젝트_데이터전송객체_목록;
    }

}
