package com.arms.jira.info.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.model.JiraInfoEntity;

public interface JiraInfo {

    public JiraInfoDTO loadConnectInfo(String connectId);

    public String getIssueTypeId(String connectId);

    public JiraInfoEntity saveConnectInfo(JiraInfoDTO jiraInfoDTO);

    public JiraInfoEntity saveIssueTypeInfo(JiraInfoEntity jiraInfoEntity);
}
