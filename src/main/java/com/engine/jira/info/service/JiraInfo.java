package com.engine.jira.info.service;

import com.engine.jira.info.model.JiraInfoDTO;
import com.engine.jira.info.model.JiraInfoEntity;

public interface JiraInfo {

    public JiraInfoDTO loadConnectInfo(String connectId);

    public String getIssueTypeId(String connectId);

    public JiraInfoEntity saveConnectInfo(JiraInfoDTO jiraInfoDTO);

    public JiraInfoEntity saveIssueTypeInfo(JiraInfoEntity jiraInfoEntity);
}
