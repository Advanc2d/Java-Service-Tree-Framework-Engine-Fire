package com.arms.cloud.jiraproject.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.arms.cloud.jiraproject.domain.CloudJiraProjectDTO;

public interface CloudJiraProject {
    public CloudJiraProjectDTO getProjectData(String projectKey) throws Exception;
    public List<CloudJiraProjectDTO> getProjectList() throws Exception;
}
