package com.arms.jira.onpremise.jirapriority.service;

import com.atlassian.jira.rest.client.api.domain.Priority;

import java.util.List;

public interface OnPremiseJiraPriority {
    List<Priority> getPriorityList(Long connectId) throws Exception;
}
