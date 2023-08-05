package com.engine.jira.cloud.jiraissue.service;

import com.engine.jira.cloud.jiraissue.domain.CloudJiraIssueDTO;
import com.engine.jira.cloud.jiraissue.domain.CloudJiraIssueInputDTO;
import com.engine.jira.cloud.jiraissue.domain.CloudJiraIssueSearchDTO;
import org.codehaus.jettison.json.JSONException;

public interface CloudJiraIssue {
    public CloudJiraIssueSearchDTO getIssueSearch(String projectKeyOrId);
    public CloudJiraIssueDTO getIssue(String issueKeyOrId);
    public String deleteIssue(String issueKeyOrId) throws JSONException;
    public String deleteIssueAndSubtask(String issueKeyOrId);
    public CloudJiraIssueDTO createIssue(CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception;
    public String updateIssue(String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO);
    //List<CloudJiraIssueDTO> getSubtasks(String issueKeyOrId);
}
