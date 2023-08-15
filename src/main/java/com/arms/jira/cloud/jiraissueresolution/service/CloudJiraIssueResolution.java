package com.arms.jira.cloud.jiraissueresolution.service;

import com.arms.jira.cloud.jiraissueresolution.model.ResolutionSearchDTO;

public interface CloudJiraIssueResolution {

    public ResolutionSearchDTO getResolutionList(Long connectId);

}
