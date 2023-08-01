package com.arms.cloud.jiraconnectinfo.service;

import com.arms.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoDTO;

public interface CloudJiraConnectInfo {
    public CloudJiraConnectInfoDTO loadConnectInfo(String id);
}
