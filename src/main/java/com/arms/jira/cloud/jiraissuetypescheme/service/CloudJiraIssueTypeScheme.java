package com.arms.jira.cloud.jiraissuetypescheme.service;

import com.arms.jira.cloud.jiraissuetypescheme.model.CloudJiraIssueTypeSchemeMappingDTO;

import java.util.Map;

public interface CloudJiraIssueTypeScheme {

    public CloudJiraIssueTypeSchemeMappingDTO getIssueTypeSchemeMapping(Long connectId);

    public Map<String,Object> addIssueTypeSchemeReqIssueType(Long connectId) throws Exception;

}
