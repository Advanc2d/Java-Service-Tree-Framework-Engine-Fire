package com.arms.jira.onpremise.jiraissueresolution.service;

import com.arms.jira.onpremise.jiraissueresolution.model.OnPremiseJiraIssueResolutionDTO;

import java.util.List;

public interface OnPremiseJiraIssueResolution {
    List<OnPremiseJiraIssueResolutionDTO> getResolutionList(Long connectId) throws Exception;
}
