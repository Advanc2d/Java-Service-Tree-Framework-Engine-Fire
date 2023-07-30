package com.arms.cloud.jiraissue.service;

import com.arms.cloud.jiraissue.domain.CloudJiraIssueDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueInputDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueSearchDTO;

public interface CloudJiraIssue {
    public CloudJiraIssueSearchDTO getIssueSearch(String projectKeyOrId);
    public CloudJiraIssueDTO getIssue(String issueKeyOrId);
    public CloudJiraIssueDTO createIssue(CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception;
}
