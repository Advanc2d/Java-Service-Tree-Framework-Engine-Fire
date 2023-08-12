package com.arms.jira.cloud.jiraissuetype.service;

import java.util.List;

import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeDTO;
import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeInputDTO;

public interface CloudJiraIssueType {

    public List<CloudJiraIssueTypeDTO> getIssueTypeListAll(String connectId) throws Exception;

    public List<CloudJiraIssueTypeDTO> getIssueTypeListByProjectId(String connectId, String projectId) throws Exception;

    public CloudJiraIssueTypeDTO createIssueType(String connectId, CloudJiraIssueTypeInputDTO cloudJiraIssueTypeInputDTO) throws Exception;

    void saveIssueTypeByUsers() throws Exception;
}
