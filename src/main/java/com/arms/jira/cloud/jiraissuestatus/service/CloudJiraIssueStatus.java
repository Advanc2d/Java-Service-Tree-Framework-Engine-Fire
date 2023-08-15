package com.arms.jira.cloud.jiraissuestatus.service;

import com.arms.jira.cloud.jiraissuestatus.model.StatusSearchDTO;

public interface CloudJiraIssueStatus {

    public StatusSearchDTO getStatusList(Long connectId);

}
