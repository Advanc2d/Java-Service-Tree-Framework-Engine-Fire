package com.engine.jira.cloud.jiraissue.service;

import com.engine.jira.cloud.jiraissue.model.CloudJiraIssueDTO;
import com.engine.jira.cloud.jiraissue.model.CloudJiraIssueInputDTO;
import com.engine.jira.cloud.jiraissue.model.CloudJiraIssueSearchDTO;
import org.codehaus.jettison.json.JSONException;

import java.util.Map;

public interface CloudJiraIssue {

    public CloudJiraIssueSearchDTO getIssueSearch(String connectId, String projectKeyOrId);

    public CloudJiraIssueDTO getIssue(String connectId, String issueKeyOrId);

    public CloudJiraIssueDTO createIssue(String connectId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception;

    public Map<String,Object> updateIssue(String connectId, String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO);

    public void deleteIssue(String connectId, String issueKeyOrId) throws Exception;

}
