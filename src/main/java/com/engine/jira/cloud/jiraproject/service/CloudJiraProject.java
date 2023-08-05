package com.engine.jira.cloud.jiraproject.service;

import java.util.List;

import com.engine.jira.cloud.jiraproject.model.CloudJiraProjectDTO;

public interface CloudJiraProject {

    public CloudJiraProjectDTO getProjectData(String projectKey, String connectId) throws Exception;

    public List<CloudJiraProjectDTO> getProjectList(String connectId) throws Exception;

}
