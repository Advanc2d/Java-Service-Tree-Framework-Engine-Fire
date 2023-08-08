package com.engine.jira.onpremise.issuetype.service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.engine.jira.info.model.JiraInfoDTO;
import com.engine.jira.info.service.JiraInfo;
import com.engine.jira.onpremise.JiraUtils;
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
