package com.arms.jira.cloud.jiraissuetype.service;

import java.util.List;

import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeDTO;
import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeInputDTO;

import reactor.core.publisher.Mono;

public interface CloudJiraIssueType {

    public Mono<List<CloudJiraIssueTypeDTO>> getNonBlockIssueTypeListAll(Long connectId) throws Exception;

    public List<CloudJiraIssueTypeDTO> getIssueTypeListAll(Long connectId) throws Exception;

    public List<CloudJiraIssueTypeDTO> getIssueTypeListByProjectId(Long connectId, String projectId) throws Exception;

    public CloudJiraIssueTypeDTO createIssueType(Long connectId, CloudJiraIssueTypeInputDTO cloudJiraIssueTypeInputDTO) throws Exception;

    void saveIssueTypeByUsers() throws Exception;
}
