package com.arms.jira.onpremise.jiraproject.service;

import com.arms.jira.onpremise.jiraproject.model.OnPremiseJiraProjectDTO;

import java.util.List;

public interface OnPremiseJiraProject {
    
    // 프로젝트 조회
    public List<OnPremiseJiraProjectDTO> getProjectList(String connectId) throws Exception;

    // 특정 프로젝트 조회
    public OnPremiseJiraProjectDTO getProject(String connectId, String projectKey) throws Exception;
    
}
