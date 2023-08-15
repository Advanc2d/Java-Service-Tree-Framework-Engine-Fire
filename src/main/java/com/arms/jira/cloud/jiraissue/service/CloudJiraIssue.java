package com.arms.jira.cloud.jiraissue.service;

import com.arms.jira.cloud.jiraissue.model.*;

import java.util.Map;

public interface CloudJiraIssue {

    public CloudJiraIssueSearchDTO getIssueSearch(Long connectId, String projectKeyOrId);

    public CloudJiraIssueDTO getIssue(Long connectId, String issueKeyOrId);

    public CloudJiraIssueDTO createIssue(Long connectId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception;

    public Map<String,Object> updateIssue(Long connectId, String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO);

    public Map<String,Object> deleteIssue(Long connectId, String issueKeyOrId) throws Exception;

    public Map<String,Object> collectLinkAndSubtask(Long connectId);

    public TransitionsDTO getIssueStatusAll(Long connectId, String issueKeyOrId);

    public Map<String,Object> updateIssueStatus(Long connectId, String issueKeyOrId, IssueStatusUpdateRequestDTO issueStatusUpdateRequestDTO);

    public PrioritySearchDTO getPriorityList(Long connectId);

    public ResolutionSearchDTO getResolutionList(Long connectId);
}
