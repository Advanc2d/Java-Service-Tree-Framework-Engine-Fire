package com.arms.jira.jiraissuetype.strategy;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.jiraissuetype.model.지라_이슈_유형_데이터_전송_객체;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class 클라우드_지라_이슈_유형_전략 implements 지라_이슈_유형_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Override
    public List<지라_이슈_유형_데이터_전송_객체> 이슈_유형_목록_가져오기(Long 연결_아이디) {
        로그.info("클라우드 지라 이슈_유형_목록_가져오기");

        String endpoint = "/rest/api/3/issuetype";

        JiraInfoDTO found = jiraInfo.checkInfo(연결_아이디);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        List<지라_이슈_유형_데이터_전송_객체> 반환할_이슈_유형_목록
                                    = CloudJiraUtils.get(webClient, endpoint,
                                    new ParameterizedTypeReference<List<지라_이슈_유형_데이터_전송_객체>>() {}).block();

        로그.info(반환할_이슈_유형_목록.toString());

        return 반환할_이슈_유형_목록;
    }

    @Override
    public List<지라_이슈_유형_데이터_전송_객체> 프로젝트별_이슈_유형_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) {

        로그.info("클라우드 지라 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈_유형_목록_가져오기");

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            /* ***
            * 에러 처리 수정 사항
            *** */
            return null;
        }

        String endpoint = "/rest/api/3/issuetype/project?projectId=" + 프로젝트_아이디;

        JiraInfoDTO found = jiraInfo.checkInfo(연결_아이디);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        List<지라_이슈_유형_데이터_전송_객체> 반환할_이슈_유형_목록
                = CloudJiraUtils.get(webClient, endpoint,
                        new ParameterizedTypeReference<List<지라_이슈_유형_데이터_전송_객체>>() {}).block();

        로그.info(반환할_이슈_유형_목록.toString());

        return 반환할_이슈_유형_목록;
    }
}
