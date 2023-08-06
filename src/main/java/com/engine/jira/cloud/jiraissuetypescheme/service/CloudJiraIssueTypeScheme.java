package com.engine.jira.cloud.jiraissuetypescheme.service;

import com.engine.jira.cloud.jiraissuetypescheme.model.CloudJiraIssueTypeSchemeMappingDTO;

import java.util.Map;

public interface CloudJiraIssueTypeScheme {

    public CloudJiraIssueTypeSchemeMappingDTO getIssueTypeSchemeMapping(String connectId);

    public Map<String,Object> addIssueTypeSchemeReqIssueType(String connectId) throws Exception;

}
