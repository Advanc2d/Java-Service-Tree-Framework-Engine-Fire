package com.arms.cloud.jiraproject.service;

import java.util.List;

import com.arms.cloud.jiraproject.domain.CloudJiraProjectDTO;

public interface CloudJiraProject {
    public CloudJiraProjectDTO getProjectData(String projectKey, String connectId) throws Exception;
    public List<CloudJiraProjectDTO> getProjectList(String connectId) throws Exception;
}
