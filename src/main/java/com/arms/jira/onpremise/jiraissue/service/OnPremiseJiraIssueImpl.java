package com.arms.jira.onpremise.jiraissue.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.arms.jira.onpremise.jiraissue.dao.OnPremiseJiraIssueJpaRepository;
import com.arms.jira.onpremise.jiraissue.model.FieldsDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueEntity;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueInputDTO;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
