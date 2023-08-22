package com.arms.jira.jiraissuestatus.strategy;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.jiraissuestatus.model.지라_이슈_상태_데이터_전송_객체;

import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Status;
import io.atlassian.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class 온프레미스_지라_이슈_상태_전략 implements 지라_이슈_상태_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Override
    public List<지라_이슈_상태_데이터_전송_객체> 이슈_상태_목록_가져오기(Long 연결_아이디) throws Exception {
        JiraInfoDTO info = jiraInfo.checkInfo(연결_아이디);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                info.getUserId(),
                info.getPasswordOrToken());

        Promise<Iterable<Status>> statusesPromise = restClient.getMetadataClient().getStatuses();
        Iterable<Status> statuses = statusesPromise.claim();

        List<지라_이슈_상태_데이터_전송_객체> 반환할_지라_이슈_상태_데이터전송객체_목록 = new ArrayList<>();
        for (Status status : statuses) {
            지라_이슈_상태_데이터_전송_객체 지라_이슈_상태_데이터_전송_객체 = new 지라_이슈_상태_데이터_전송_객체();
            지라_이슈_상태_데이터_전송_객체.setSelf(status.getSelf().toString());
            지라_이슈_상태_데이터_전송_객체.setId(status.getId().toString());
            지라_이슈_상태_데이터_전송_객체.setName(status.getName());
            지라_이슈_상태_데이터_전송_객체.setDescription(status.getDescription());
            반환할_지라_이슈_상태_데이터전송객체_목록.add(지라_이슈_상태_데이터_전송_객체);
        }

        return 반환할_지라_이슈_상태_데이터전송객체_목록;
    }

    @Override
    public List<지라_이슈_상태_데이터_전송_객체> 프로젝트별_이슈_상태_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws Exception {

        로그.info("온프레미스 이슈_상태_목록_가져오기 실행");

        JiraInfoDTO info = jiraInfo.checkInfo(연결_아이디);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                                                                        info.getUserId(),
                                                                        info.getPasswordOrToken());

        Promise<Iterable<Status>> statusesPromise = restClient.getMetadataClient().getStatuses();
        Iterable<Status> statuses = statusesPromise.claim();

        List<지라_이슈_상태_데이터_전송_객체> 반환할_지라_이슈_상태_데이터전송객체_목록 = new ArrayList<>();
        for (Status status : statuses) {
            지라_이슈_상태_데이터_전송_객체 지라_이슈_상태_데이터_전송_객체 = new 지라_이슈_상태_데이터_전송_객체();
            지라_이슈_상태_데이터_전송_객체.setSelf(status.getSelf().toString());
            지라_이슈_상태_데이터_전송_객체.setId(status.getId().toString());
            지라_이슈_상태_데이터_전송_객체.setName(status.getName());
            지라_이슈_상태_데이터_전송_객체.setDescription(status.getDescription());
            반환할_지라_이슈_상태_데이터전송객체_목록.add(지라_이슈_상태_데이터_전송_객체);
        }

        return 반환할_지라_이슈_상태_데이터전송객체_목록;
    }

}
