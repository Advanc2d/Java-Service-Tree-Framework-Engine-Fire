package com.arms.cloud.jiraissuetypescheme.service;

import com.arms.cloud.jiraissuetypescheme.domain.CloudJiraIssueTypeSchemeMappingDTO;

public interface CloudJiraIssueTypeScheme {
    public CloudJiraIssueTypeSchemeMappingDTO getIssueTypeSchemeMapping();
    public String addIssueTypeSchemeReqIssueType();
}
