package com.engine.jira.cloud.jiraconnectinfo.service;

import com.engine.jira.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoDTO;

public interface CloudJiraConnectInfo {
    public CloudJiraConnectInfoDTO loadConnectInfo(String id);
}
