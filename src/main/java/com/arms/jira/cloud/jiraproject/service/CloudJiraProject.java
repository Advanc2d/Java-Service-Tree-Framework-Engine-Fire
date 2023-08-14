package com.arms.jira.cloud.jiraproject.service;

import java.util.List;

import com.arms.jira.cloud.jiraproject.model.CloudJiraProjectDTO;

public interface CloudJiraProject {

    public CloudJiraProjectDTO getProjectData(Long connectId, String projectKey) throws Exception;

    public List<CloudJiraProjectDTO> getProjectList(Long connectId) throws Exception;

}
