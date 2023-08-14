package com.arms.jira.onpremise.jiraissuetype.service;

import com.atlassian.jira.rest.client.api.domain.IssueType;

import java.util.List;
import java.util.Map;

public interface OnPremiseJiraIssueType {

    public List<IssueType> getOnPremiseIssueTypeListAll(Long connectId) throws Exception;

    public IssueType getIssueTypeListByIssueTypeId(Long connectId, String issueTypeId) throws Exception;

    public Map<String, Object> checkReqIssueType(Long connectId) throws Exception;

}
