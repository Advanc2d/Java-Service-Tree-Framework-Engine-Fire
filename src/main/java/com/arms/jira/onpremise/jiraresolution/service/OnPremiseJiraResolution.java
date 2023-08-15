package com.arms.jira.onpremise.jiraresolution.service;

import com.atlassian.jira.rest.client.api.domain.Resolution;

import java.util.List;

public interface OnPremiseJiraResolution {
    List<Resolution> getResolutionList(Long connectId) throws Exception;
}
