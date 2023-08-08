package com.arms.jira.onpremise.issuetype.service;

import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.onpremise.JiraUtils;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.arms.jira.info.model.JiraInfoDTO;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service("onPremiseJiraIssueType")
public class OnPremiseJiraIssueTypeImpl implements OnPremiseJiraIssueType{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Override
    public String getOnPremiseIssueTypeList(String connectId) throws Exception {
        JiraInfoDTO jiraInfoDTO = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = JiraUtils.getJiraRestClient(jiraInfoDTO.getUri(),
                                                                jiraInfoDTO.getUserId(),
                                                                jiraInfoDTO.getPasswordOrToken());

        return null;
    }

}
