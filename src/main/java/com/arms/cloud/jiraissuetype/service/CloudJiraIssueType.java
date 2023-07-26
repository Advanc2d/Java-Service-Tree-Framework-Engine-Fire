package com.arms.cloud.jiraissuetype.service;

import java.util.List;

import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeDTO;
import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeInputDTO;

public interface CloudJiraIssueType {
    public List<CloudJiraIssueTypeDTO> getIssueTypeList() throws Exception;
    public CloudJiraIssueTypeDTO createIssueType(CloudJiraIssueTypeInputDTO cloudJiraIssueTypeInputDTO) throws Exception;
}
