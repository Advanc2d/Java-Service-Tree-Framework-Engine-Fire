package com.arms.jira.jiraproject.strategy;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraproject.model.CloudJiraProjectDTO;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.jiraproject.model.지라_프로젝트_데이터_전송_객체;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class 클라우드_지라_프로젝트_전략 implements 지라_프로젝트_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Override
    public 지라_프로젝트_데이터_전송_객체 프로젝트_상세정보_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {
        로그.info("클라우드 지라 프로젝트 "+ 프로젝트_키_또는_아이디 +" 상세정보 가져오기");

        String endpoint = "/rest/api/3/project/"+ 프로젝트_키_또는_아이디;

        JiraInfoDTO found = jiraInfo.loadConnectInfo(연결_아이디);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        지라_프로젝트_데이터_전송_객체 반환할_지라_프로젝트_상세정보 = CloudJiraUtils.get(webClient, endpoint, 지라_프로젝트_데이터_전송_객체.class).block();

        로그.info(반환할_지라_프로젝트_상세정보.toString());

        return 반환할_지라_프로젝트_상세정보;
    }

    @Override
    public List<지라_프로젝트_데이터_전송_객체> 프로젝트_전체_목록_가져오기(Long 연결_아이디) {
        로그.info("클라우드 지라 프로젝트 전체 목록 가져오기");

        String endpoint = "/rest/api/3/project";

        JiraInfoDTO found = jiraInfo.checkInfo(연결_아이디);

        if (found == null) {
            // throw Exception e; ControllerAdvice 오류 처리
        }

        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        List<지라_프로젝트_데이터_전송_객체> 반환할_지라_프로젝트_목록
                        = CloudJiraUtils.get(webClient, endpoint,
                                new ParameterizedTypeReference<List<지라_프로젝트_데이터_전송_객체>>() {}).block();

        로그.info(반환할_지라_프로젝트_목록.toString());

        return 반환할_지라_프로젝트_목록;
    }

}
