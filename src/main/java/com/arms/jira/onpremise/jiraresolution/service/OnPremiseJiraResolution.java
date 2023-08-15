package com.arms.jira.onpremise.jiraresolution.service;

import com.arms.jira.onpremise.jiraresolution.model.OnPremiseJiraResolutionDTO;

import java.util.List;

public interface OnPremiseJiraResolution {
    List<OnPremiseJiraResolutionDTO> getResolutionList(Long connectId) throws Exception;
}
