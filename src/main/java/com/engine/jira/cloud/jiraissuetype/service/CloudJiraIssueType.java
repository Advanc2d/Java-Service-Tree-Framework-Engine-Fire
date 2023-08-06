package com.engine.jira.cloud.jiraissuetype.service;

import java.util.List;

import com.engine.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeDTO;
import com.engine.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeInputDTO;

public interface CloudJiraIssueType {

    public CloudJiraIssueTypeDTO createIssueType(String connectId, CloudJiraIssueTypeInputDTO cloudJiraIssueTypeInputDTO) throws Exception;

    public List<CloudJiraIssueTypeDTO> getIssueTypeListByCloud(String connectId) throws Exception;

}
