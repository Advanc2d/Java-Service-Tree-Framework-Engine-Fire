package com.engine.jira.cloud.jiraissue.service;

import com.engine.jira.cloud.jiraissue.model.CloudJiraIssueDTO;
import com.engine.jira.cloud.jiraissue.model.CloudJiraIssueInputDTO;
import com.engine.jira.cloud.jiraissue.model.CloudJiraIssueSearchDTO;
import org.codehaus.jettison.json.JSONException;

public interface CloudJiraIssue {
    public CloudJiraIssueSearchDTO getIssueSearch(String projectKeyOrId);
    public CloudJiraIssueDTO getIssue(String issueKeyOrId);
    public void deleteIssue(String issueKeyOrId) throws Exception;

    public CloudJiraIssueDTO createIssue(CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception;
    public String updateIssue(String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO);

}
