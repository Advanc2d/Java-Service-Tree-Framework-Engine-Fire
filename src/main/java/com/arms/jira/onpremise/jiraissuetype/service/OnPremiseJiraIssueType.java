package com.arms.jira.onpremise.jiraissuetype.service;

import com.arms.jira.onpremise.jiraissuetype.model.OnPremiseJiraIssueTypeDTO;
import java.util.List;
import java.util.Map;

public interface OnPremiseJiraIssueType {

    public List<OnPremiseJiraIssueTypeDTO> getOnPremiseIssueTypeListAll(Long connectId) throws Exception;

    public OnPremiseJiraIssueTypeDTO getIssueTypeListByIssueTypeId(Long connectId, String issueTypeId) throws Exception;

    public Map<String, Object> checkReqIssueType(Long connectId) throws Exception;

}
