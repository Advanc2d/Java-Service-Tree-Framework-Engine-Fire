package com.arms.cloud.jiraissuetypescheme.service;

import com.arms.cloud.jiraissuetypescheme.domain.CloudJiraIssueTypeSchemeMappingDTO;
import java.util.Map;

public interface CloudJiraIssueTypeScheme {
    public CloudJiraIssueTypeSchemeMappingDTO getIssueTypeSchemeMapping(String connectId);
    public Map<String,Object> addIssueTypeSchemeReqIssueType(String connectId) throws Exception;
}
