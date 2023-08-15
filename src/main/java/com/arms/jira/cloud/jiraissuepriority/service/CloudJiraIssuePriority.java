package com.arms.jira.cloud.jiraissuepriority.service;

import com.arms.jira.cloud.jiraissuepriority.model.PrioritySearchDTO;

public interface CloudJiraIssuePriority {

    public PrioritySearchDTO getPriorityList(Long connectId);

}
