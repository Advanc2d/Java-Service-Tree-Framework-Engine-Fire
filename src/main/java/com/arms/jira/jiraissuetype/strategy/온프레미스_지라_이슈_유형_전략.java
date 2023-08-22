package com.arms.jira.jiraissuetype.strategy;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.jiraissuetype.model.지라_이슈_유형_데이터_전송_객체;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class 온프레미스_지라_이슈_유형_전략 implements 지라_이슈_유형_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Override
    public List<지라_이슈_유형_데이터_전송_객체> 이슈_유형_목록_가져오기(Long 연결_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {

        로그.info("온프레미스 지라 이슈_유형_목록_가져오기");

        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(연결정보.getUri(),
                                                                        연결정보.getUserId(),
                                                                        연결정보.getPasswordOrToken());

        Iterable<IssueType> 온프라미스_이슈_유형_목록 = restClient.getMetadataClient().getIssueTypes().get();
        List<지라_이슈_유형_데이터_전송_객체> 반환할_이슈_유형_목록 = new ArrayList<>();

        for (IssueType 온프라미스_이슈_유형 : 온프라미스_이슈_유형_목록) {
            지라_이슈_유형_데이터_전송_객체 지라_이슈_유형_데이터_전송_객체 = new 지라_이슈_유형_데이터_전송_객체();

            지라_이슈_유형_데이터_전송_객체.setId(온프라미스_이슈_유형.getId().toString());
            지라_이슈_유형_데이터_전송_객체.setName(온프라미스_이슈_유형.getName());
            지라_이슈_유형_데이터_전송_객체.setSelf(온프라미스_이슈_유형.getName());
            지라_이슈_유형_데이터_전송_객체.setSubtask(온프라미스_이슈_유형.isSubtask());
            지라_이슈_유형_데이터_전송_객체.setDescription(온프라미스_이슈_유형.getDescription());

            반환할_이슈_유형_목록.add(지라_이슈_유형_데이터_전송_객체);
        }

        로그.info(반환할_이슈_유형_목록.toString());

        return 반환할_이슈_유형_목록;
    }
}
