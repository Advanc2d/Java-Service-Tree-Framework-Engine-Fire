package com.arms.jira.onpremise.jiraissue.service;

import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueInputDTO;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueInputDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueDTO;


import java.util.Map;
public interface OnPremiseJiraIssue {


    OnPremiseJiraIssueDTO getIssueSearch(String connectId, String projectKeyOrId) throws Exception;

    String getIssue(String connectId, String issueKeyOrId);

    Map<String, Object> updateIssue(String connectId, String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO);

    // 이슈 생성
    public OnPremiseJiraIssueDTO createIssue(String connectId, OnPremiseJiraIssueInputDTO onPremiseJiraIssueInputDTO) throws Exception;


}
