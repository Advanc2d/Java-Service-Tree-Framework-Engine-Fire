package com.arms.jira.onpremise.jiraissue.service;


import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueDTO;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueInputDTO;

import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueSearchDTO;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueSearchDTO;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service("jiraIssue")
public class OnPremiseJiraIssueImpl implements OnPremiseJiraIssue{

    @Autowired
    private JiraInfo jiraInfo;
    @Override
    public OnPremiseJiraIssueSearchDTO getIssueSearch(String connectId, String projectKeyOrId) throws Exception{

        JiraInfoDTO info = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                info.getUserId(),
                info.getPasswordOrToken());

        String jql = "project = " + projectKeyOrId;
        int maxResults = 10; // 최대 검색 결과 개수
        int startAt = 0; // 시작 지점
        boolean isLast = false;
        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색할 필드
        ArrayList<Issue> issueList = new ArrayList<>();
        while (!isLast) {


        }

        return new OnPremiseJiraIssueSearchDTO(issueList);

    }

    @Override
    public String getIssue(String connectId, String issueKeyOrId) {
        return null;
    }

    @Override
    public Map<String, Object> updateIssue(String connectId, String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO) {
        return null;
    }
}
