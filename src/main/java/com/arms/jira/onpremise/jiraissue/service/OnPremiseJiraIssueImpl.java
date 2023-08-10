package com.arms.jira.onpremise.jiraissue.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.arms.jira.onpremise.jiraissue.dao.OnPremiseJiraIssueJpaRepository;
import com.arms.jira.onpremise.jiraissue.model.*;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@AllArgsConstructor
@Service("onPremiseJiraIssue")
public class OnPremiseJiraIssueImpl implements OnPremiseJiraIssue {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OnPremiseJiraIssueJpaRepository onPremiseJiraIssueJpaRepository;

    @Transactional
    @Override
    public OnPremiseJiraIssueDTO createIssue(String connectId, OnPremiseJiraIssueInputDTO onPremiseJiraIssueInputDTO) throws Exception {

        JiraInfoDTO info = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                                                                         info.getUserId(),
                                                                         info.getPasswordOrToken());

        // 입력 값 받아오기
        FieldsDTO fieldsDTO = onPremiseJiraIssueInputDTO.getFields();
        String projectKey = fieldsDTO.getProject().getKey();
        Long issueTypeId = Long.valueOf(fieldsDTO.getIssuetype().getId());
        String summary = fieldsDTO.getSummary();
        String description = fieldsDTO.getDescription();
        String reporter = fieldsDTO.getReporter() != null ? fieldsDTO.getReporter().getName() : null;
        String assignee = fieldsDTO.getAssignee() != null ? fieldsDTO.getAssignee().getName() : null;

        // IssueInput 타입의 입력 값 생성
        IssueInputBuilder issueInputBuilder = new IssueInputBuilder(projectKey, issueTypeId, summary);
        issueInputBuilder.setDescription(description);
        if (reporter != null) {
            issueInputBuilder.setReporterName(reporter);
        }
        if (assignee != null) {
            issueInputBuilder.setAssigneeName(assignee);
        }
        IssueInput issueInput = issueInputBuilder.build();

        // 이슈 생성
        BasicIssue issue = restClient.getIssueClient().createIssue(issueInput).claim();
        logger.info("id: " + issue.getId());
        logger.info("key: " + issue.getKey());
        logger.info("self: " + issue.getSelf());

        OnPremiseJiraIssueDTO onPremiseJiraIssueDTO = new OnPremiseJiraIssueDTO();
        onPremiseJiraIssueDTO.setId(issue.getId().toString());
        onPremiseJiraIssueDTO.setKey(issue.getKey());
        onPremiseJiraIssueDTO.setSelf(issue.getSelf().toString());

        // DB 저장
        OnPremiseJiraIssueEntity onPremiseJiraIssueEntity = modelMapper.map(onPremiseJiraIssueDTO, OnPremiseJiraIssueEntity.class);

        onPremiseJiraIssueEntity.setConnectId(connectId);
        onPremiseJiraIssueJpaRepository.save(onPremiseJiraIssueEntity);

        return onPremiseJiraIssueDTO;
    }
    @Override
    public SearchResult getIssueSearch(String connectId, String projectKeyOrId) throws Exception {
        JiraInfoDTO info = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                info.getUserId(),
                info.getPasswordOrToken());

        String jql = "project = " + projectKeyOrId;
        int maxResults = 10;
        int startAt = 0;
        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드
        SearchResult tempResult = restClient.getSearchClient().searchJql(jql, maxResults, startAt, fields).get();

        int totalIssues = tempResult.getTotal();
        maxResults = totalIssues;
        SearchResult result = restClient.getSearchClient().searchJql(jql, maxResults, startAt, fields).get();

        return result;
    }

    @Override
    public Issue getIssue(String connectId, String issueKeyOrId) throws Exception {

        JiraInfoDTO info = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                info.getUserId(),
                info.getPasswordOrToken());

        Issue issue = restClient.getIssueClient().getIssue(issueKeyOrId).claim();
        
        return issue;
    }

    @Override
    public Map<String, Object> updateIssue(String connectId, String issueKeyOrId, OnPremiseJiraIssueInputDTO onPremiseJiraIssueInputDTO) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
        JiraInfoDTO info = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                info.getUserId(),
                info.getPasswordOrToken());

        FieldsDTO fields = onPremiseJiraIssueInputDTO.getFields();

        IssueInputBuilder issueInputBuilder = new IssueInputBuilder();

        // summary,description,Priority 업데이트 개발 완료
        if (fields.getSummary() != null) { //요약
            issueInputBuilder.setSummary(fields.getSummary());
        }
        if (fields.getDescription() != null) { // 설명
            issueInputBuilder.setDescription(fields.getDescription());
        }

        if (fields.getPriority() != null && fields.getPriority().getId() != null) {//우선순위
            issueInputBuilder.setFieldValue("priority", ComplexIssueInputFieldValue.with("id", fields.getPriority().getId()));
        }

//        if (fields.getAssignee() != null && fields.getAssignee().getName() != null) { //담당자
//            issueInputBuilder.setAssigneeName(fields.getAssignee().getName());
//        }
//        if (fields.getReporter() != null && fields.getReporter().getName() != null) { //보고자
//            issueInputBuilder.setReporterName(fields.getReporter().getName());
//        }
        IssueInput issueInput = issueInputBuilder.build();

        // 이슈 업데이트 실행
        restClient.getIssueClient().updateIssue(issueKeyOrId, issueInput).claim();
        resultMap.put("updateStatus", "success");
        return resultMap;
        } catch (Exception e) { // 업데이트 실패한 경우
            resultMap.put("updateStatus", "failed");
            resultMap.put("errorMessage", e.getMessage());
        }
        return resultMap;
    }

}
