package com.arms.cloud.jiraissue.service;

import com.arms.cloud.jiraissue.domain.CloudJiraIssueDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueInputDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueSearchDTO;
import org.codehaus.jettison.json.JSONException;

import java.util.List;

public interface CloudJiraIssue {
    public CloudJiraIssueSearchDTO getIssueSearch(String projectKeyOrId);
    public CloudJiraIssueDTO getIssue(String issueKeyOrId);
    public void deleteIssue(String issueKeyOrId) throws Exception;

    public CloudJiraIssueDTO createIssue(CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception;
    public String updateIssue(String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO);

}
