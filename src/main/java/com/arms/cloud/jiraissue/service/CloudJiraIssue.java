package com.arms.cloud.jiraissue.service;

import com.arms.cloud.jiraissue.domain.CloudJiraIssueDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueInputDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueSearchDTO;
import org.codehaus.jettison.json.JSONException;

import java.util.List;

public interface CloudJiraIssue {
    public CloudJiraIssueSearchDTO getIssueSearch(String projectKeyOrId);
    public CloudJiraIssueDTO getIssue(String issueKeyOrId);
    public String deleteIssue(String issueKeyOrId) throws JSONException;
    public String deleteIssueAndSubtask(String issueKeyOrId);
    public CloudJiraIssueDTO createIssue(CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception;

    //List<CloudJiraIssueDTO> getSubtasks(String issueKeyOrId);
}
