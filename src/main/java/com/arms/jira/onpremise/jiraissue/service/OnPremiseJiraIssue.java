package com.arms.jira.onpremise.jiraissue.service;

import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueInputDTO;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
public interface OnPremiseJiraIssue {


    // 이슈 생성
    OnPremiseJiraIssueDTO createIssue(String connectId, OnPremiseJiraIssueInputDTO onPremiseJiraIssueInputDTO) throws Exception;

    // 프로젝트 이슈 조회
    JsonNode getIssueSearch(String connectId, String projectKeyOrId) throws Exception;

    // 이슈 정보 상세 조회
    Issue getIssue(String connectId, String issueKeyOrId) throws Exception;

    // 이슈 업데이트
    Map<String, Object> updateIssue(String connectId, String issueKeyOrId, OnPremiseJiraIssueInputDTO onPremiseJiraIssueInputDTO) throws Exception;

    // 이슈 삭제
    Map<String, Object> deleteIssue(String connectId, String issueKey) throws Exception;
}
