package com.arms.jira.onpremise.jiraissue.service;

import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueInputDTO;

public interface OnPremiseJiraIssue {

    // 이슈 생성
    public OnPremiseJiraIssueDTO createIssue(String connectId, OnPremiseJiraIssueInputDTO onPremiseJiraIssueInputDTO) throws Exception;
}
