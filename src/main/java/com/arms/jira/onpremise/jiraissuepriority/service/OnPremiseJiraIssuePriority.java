package com.arms.jira.onpremise.jiraissuepriority.service;

import com.arms.jira.onpremise.jiraissuepriority.model.OnPremiseJiraIssuePriorityDTO;

import java.util.List;

public interface OnPremiseJiraIssuePriority {
    List<OnPremiseJiraIssuePriorityDTO> getPriorityList(Long connectId) throws Exception;
}
