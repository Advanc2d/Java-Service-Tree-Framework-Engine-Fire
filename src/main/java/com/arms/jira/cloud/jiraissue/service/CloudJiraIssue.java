package com.arms.jira.cloud.jiraissue.service;

import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueDTO;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueInputDTO;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueSearchDTO;

import java.util.Map;

public interface CloudJiraIssue {

    public CloudJiraIssueSearchDTO getIssueSearch(String connectId, String projectKeyOrId);

    public CloudJiraIssueDTO getIssue(String connectId, String issueKeyOrId);

    public CloudJiraIssueDTO createIssue(String connectId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception;

    public Map<String,Object> updateIssue(String connectId, String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO);

    public Map<String,Object> deleteIssue(String connectId, String issueKeyOrId) throws Exception;

    public Map<String,Object> collectLinkAndSubtask(String connectId);

}
