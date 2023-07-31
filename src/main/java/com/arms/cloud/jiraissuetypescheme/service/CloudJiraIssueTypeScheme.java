package com.arms.cloud.jiraissuetypescheme.service;

import com.arms.cloud.jiraissuetypescheme.domain.CloudJiraIssueTypeSchemeMappingDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CloudJiraIssueTypeScheme {
    public CloudJiraIssueTypeSchemeMappingDTO getIssueTypeSchemeMapping();
    public List<ResponseEntity<?>> addIssueTypeSchemeReqIssueType() throws Exception;
}
