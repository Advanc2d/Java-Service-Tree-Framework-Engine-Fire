package com.arms.jira.onpremise.jiraissue.service;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueEntity;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.arms.jira.onpremise.jiraissue.dao.OnPremiseJiraIssueJpaRepository;
import com.arms.jira.onpremise.jiraissue.model.FieldsDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueEntity;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueInputDTO;
import com.arms.jira.onpremise.jiraissuepriority.model.OnPremiseJiraIssuePriorityDTO;
import com.arms.jira.onpremise.jiraissueresolution.model.OnPremiseJiraIssueResolutionDTO;
import com.arms.jira.onpremise.jiraissuestatus.model.OnPremiseJiraIssueStatusDTO;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


import java.sql.Timestamp;
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
    public OnPremiseJiraIssueDTO createIssue(Long connectId, OnPremiseJiraIssueInputDTO onPremiseJiraIssueInputDTO) throws Exception {

        JiraInfoDTO info = jiraInfo.checkInfo(connectId);

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
    public JsonNode getIssueSearch(Long connectId, String projectKeyOrId) throws Exception {

        JiraInfoDTO info = jiraInfo.checkInfo(connectId);

        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                info.getUserId(),
                info.getPasswordOrToken());

        String jql = "project = " + projectKeyOrId;
        int maxResults = 1000;
        int startAt = 0;
        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        // 이슈 건수가 1000이 넘을때 이슈 조회를 위한 처리
        List<Issue> allIssues = new ArrayList<>();
        SearchResult searchResult;
        do {
            searchResult = restClient.getSearchClient()
                    .searchJql(jql, maxResults, startAt, fields)
                    .get();
            for (Issue issue : searchResult.getIssues()) {
                allIssues.add(issue);
            }
            startAt += maxResults;
        } while (searchResult.getTotal() > startAt);

        // 변환을 위한 ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule()); //Date 처리 위함
        // 이슈 리스트를 json 형식으로 변환
        JsonNode issuesAsJson = null;
        try {
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("total", allIssues.size());
            resultData.put("issues", allIssues);

            issuesAsJson = objectMapper.valueToTree(resultData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return issuesAsJson;
    }

//    @Override
//    public Issue getIssue(Long connectId, String issueKeyOrId) throws Exception {
//        JiraInfoDTO info = jiraInfo.checkInfo(connectId);
//
//        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
//                info.getUserId(),
//                info.getPasswordOrToken());
//        try {
//            Issue issue = restClient.getIssueClient().getIssue(issueKeyOrId).claim();
//            return issue;
//        }catch (RestClientException e) {
//            logger.info("이슈 조회시 오류가 발생하였습니다.");
//            throw new RuntimeException("이슈 조회시 오류가 발생하였습니다.");
//        }
//    }

    @Override
    public OnPremiseJiraIssueDTO getIssue(Long connectId, String issueKeyOrId) throws Exception {
        JiraInfoDTO info = jiraInfo.checkInfo(connectId);

        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                info.getUserId(),
                info.getPasswordOrToken());
        try {
            OnPremiseJiraIssueDTO onPremiseJiraIssueDTO = new OnPremiseJiraIssueDTO();
            FieldsDTO fieldsDTO = new FieldsDTO();


            FieldsDTO.Project project = FieldsDTO.Project.builder().build();

            FieldsDTO.IssueType issueType = FieldsDTO.IssueType.builder().build();

            FieldsDTO.Reporter reporter = FieldsDTO.Reporter.builder().build();

            FieldsDTO.Assignee assignee = FieldsDTO.Assignee.builder().build();

            OnPremiseJiraIssueDTO inwardIssue = new OnPremiseJiraIssueDTO();
            OnPremiseJiraIssueDTO outwardIssue = new OnPremiseJiraIssueDTO();
            FieldsDTO.IssueLink IssueLink = new FieldsDTO.IssueLink();
            IssueLink.setInwardIssue(inwardIssue);
            IssueLink.setOutwardIssue(outwardIssue);
            List<FieldsDTO.IssueLink> IssueLinkList = new ArrayList<>();

            fieldsDTO.setProject(project);
            fieldsDTO.setIssuetype(issueType);
            fieldsDTO.setReporter(reporter);
            fieldsDTO.setAssignee(assignee);
            fieldsDTO.setIssuelinks(IssueLinkList);


            onPremiseJiraIssueDTO.setFields(fieldsDTO);

            Issue issue = restClient.getIssueClient().getIssue(issueKeyOrId).claim();

            onPremiseJiraIssueDTO.setId(issue.getId().toString());
            onPremiseJiraIssueDTO.setKey(issue.getKey());
            onPremiseJiraIssueDTO.setSelf(issue.getSelf().toString());

            // 필드 하위 프로젝트
            onPremiseJiraIssueDTO.getFields().getProject().setSelf(issue.getProject().getSelf().toString());
            onPremiseJiraIssueDTO.getFields().getProject().setId(String.valueOf(issue.getProject().getId()));
            onPremiseJiraIssueDTO.getFields().getProject().setKey(issue.getProject().getKey());
            onPremiseJiraIssueDTO.getFields().getProject().setName(issue.getProject().getName());

            // 필드 하위 이슈 타입
            onPremiseJiraIssueDTO.getFields().getIssuetype().setSelf(String.valueOf(issue.getIssueType().getSelf()));
            onPremiseJiraIssueDTO.getFields().getIssuetype().setId(String.valueOf(issue.getIssueType().getId()));
            onPremiseJiraIssueDTO.getFields().getIssuetype().setName(issue.getIssueType().getName());
            // 이슈 summary
            onPremiseJiraIssueDTO.getFields().setSummary(issue.getSummary());

            // 이슈 description
            onPremiseJiraIssueDTO.getFields().setDescription(issue.getDescription());

            // 이슈 보고자
            onPremiseJiraIssueDTO.getFields().getReporter().setName(issue.getReporter().getName());
            onPremiseJiraIssueDTO.getFields().getReporter().setEmailAddress(issue.getReporter().getEmailAddress());

            // 이슈 담당자
            onPremiseJiraIssueDTO.getFields().getAssignee().setName(issue.getAssignee().getName());
            onPremiseJiraIssueDTO.getFields().getAssignee().setEmailAddress(issue.getAssignee().getEmailAddress());

            // 이슈 라벨
            Set<String> labelsSet = issue.getLabels(); //HashSet 반환
            if (labelsSet != null) {
                List<String> labelsList = new ArrayList<>(labelsSet);
                onPremiseJiraIssueDTO.getFields().setLabels(labelsList);
            } else {
                onPremiseJiraIssueDTO.getFields().setLabels(Collections.emptyList());
            }


            // 이슈 링크
            List<IssueLink> issueLinksList = new ArrayList<>((Collection) issue.getIssueLinks());
            for (IssueLink issueLink : issueLinksList) {

                String direction = String.valueOf(issueLink.getIssueLinkType().getDirection());
                String targetIssueKey = issueLink.getTargetIssueKey();
                String self = String.valueOf(issueLink.getTargetIssueUri());
                String[] parts = self.split("/");
                String id = parts[parts.length - 1];

                FieldsDTO.IssueLink fieldsIssueLink = new FieldsDTO.IssueLink();
                OnPremiseJiraIssueDTO inwardIssue_for_object = new OnPremiseJiraIssueDTO();
                OnPremiseJiraIssueDTO outwardIssue_for_object  = new OnPremiseJiraIssueDTO();

                fieldsIssueLink.setInwardIssue(inwardIssue_for_object);
                fieldsIssueLink.setOutwardIssue(outwardIssue_for_object);


                if(direction.equals("INBOUND")){
                    logger.info("direction   "+direction+"   targetIssueKey   "+targetIssueKey);
                    inwardIssue_for_object.setKey(targetIssueKey);
                    inwardIssue_for_object.setSelf(self);
                    inwardIssue_for_object.setId(id);
                }
                else if(direction.equals("OUTBOUND")){
                    logger.info("direction   "+direction+"   targetIssueKey   "+targetIssueKey);
                    outwardIssue_for_object.setKey(targetIssueKey);
                    outwardIssue_for_object.setSelf(self);
                    outwardIssue_for_object.setId(id);
                }

                onPremiseJiraIssueDTO.getFields().getIssuelinks().add(fieldsIssueLink);

            }

            // 서브 테스크
            Iterable<Subtask> subtasks_form_jira = issue.getSubtasks();
            List<Subtask> subtaskList = new ArrayList<>();
            for (Subtask subtask : subtasks_form_jira) {
                subtaskList.add(subtask);
            }
            for(Subtask subtask : subtaskList){

                String key = subtask.getIssueKey();
                String self = String.valueOf(subtask.getIssueUri());
                String[] self_parts = self.split("/");
                String id = self_parts[self_parts.length - 1];

                String issuetype_self = String.valueOf(subtask.getIssueType().getSelf());
                String[] issuetype_self_parts = issuetype_self.split("/");
                String issuetype_id = issuetype_self_parts[issuetype_self_parts.length - 1];
                String issuetype_name = subtask.getIssueType().getName();

                String summary = subtask.getSummary();

                String status_self = String.valueOf(subtask.getStatus().getSelf());
                String status_name = subtask.getStatus().getName();
                String[] status_self_parts = status_self.split("/");
                String status_id = status_self_parts[status_self_parts.length - 1];
                String status_description = subtask.getStatus().getDescription();

                OnPremiseJiraIssueDTO subtask_for_object = new OnPremiseJiraIssueDTO();
                FieldsDTO subtask_fieldsDTO = new FieldsDTO();
                subtask_for_object.setFields(subtask_fieldsDTO);
                subtask_for_object.getFields().setIssuetype(new FieldsDTO.IssueType());
                subtask_for_object.getFields().setPriority(new OnPremiseJiraIssuePriorityDTO());
                subtask_for_object.getFields().setStatus(new OnPremiseJiraIssueStatusDTO());

                subtask_for_object.setId(id);
                subtask_for_object.setKey(key);
                subtask_for_object.setSelf(self);

                subtask_for_object.getFields().getIssuetype().setSelf(issuetype_self);
                subtask_for_object.getFields().getIssuetype().setId(issuetype_id);
                subtask_for_object.getFields().getIssuetype().setName(issuetype_name);

                subtask_for_object.getFields().setSummary(summary);

                subtask_for_object.getFields().getStatus().setId(status_id);
                subtask_for_object.getFields().getStatus().setName(status_name);
                subtask_for_object.getFields().getStatus().setDescription(status_description);
                subtask_for_object.getFields().getStatus().setSelf(status_self);

                if (onPremiseJiraIssueDTO.getFields().getSubtasks() == null) {
                    onPremiseJiraIssueDTO.getFields().setSubtasks(new ArrayList<>());
                }
                onPremiseJiraIssueDTO.getFields().getSubtasks().add(subtask_for_object);
            }

            // 우선 순위
            OnPremiseJiraIssuePriorityDTO priority = new OnPremiseJiraIssuePriorityDTO();
            fieldsDTO.setPriority(priority);
            onPremiseJiraIssueDTO.setFields(fieldsDTO);

            String id = String.valueOf(issue.getPriority().getId());
            String name = issue.getPriority().getName();
            String self = String.valueOf(issue.getPriority().getSelf());

            onPremiseJiraIssueDTO.getFields().getPriority().setSelf(self);
            onPremiseJiraIssueDTO.getFields().getPriority().setId(id);
            onPremiseJiraIssueDTO.getFields().getPriority().setName(name);

            // 상태 값
            OnPremiseJiraIssueStatusDTO status =new OnPremiseJiraIssueStatusDTO();
            fieldsDTO.setStatus(status);
            onPremiseJiraIssueDTO.setFields(fieldsDTO);

            String status_id = String.valueOf(issue.getStatus().getId());
            String status_name = issue.getStatus().getName();
            String status_description =  issue.getStatus().getDescription();
            String status_self = String.valueOf(issue.getStatus().getSelf());

            onPremiseJiraIssueDTO.getFields().getStatus().setId(status_id);
            onPremiseJiraIssueDTO.getFields().getStatus().setName(status_name);
            onPremiseJiraIssueDTO.getFields().getStatus().setDescription(status_description);
            onPremiseJiraIssueDTO.getFields().getStatus().setSelf(status_self);

            // 해결책
            if(issue.getResolution()!= null){
                OnPremiseJiraIssueResolutionDTO resolution =new OnPremiseJiraIssueResolutionDTO();
                fieldsDTO.setResolution(resolution);
                onPremiseJiraIssueDTO.setFields(fieldsDTO);

                String resol_id = String.valueOf(issue.getResolution().getId());
                String resol_self = String.valueOf(issue.getResolution().getSelf());
                String resol_name =issue.getResolution().getName();
                String resol_description = issue.getResolution().getDescription();
                onPremiseJiraIssueDTO.getFields().getResolution().setDescription(resol_description);
                onPremiseJiraIssueDTO.getFields().getResolution().setId(resol_id);
                onPremiseJiraIssueDTO.getFields().getResolution().setName(resol_name);
                onPremiseJiraIssueDTO.getFields().getResolution().setSelf(resol_self);
            }

            return onPremiseJiraIssueDTO;
        }catch (RestClientException e) {
            logger.info("이슈 조회시 오류가 발생하였습니다.");
            throw new RuntimeException("이슈 조회시 오류가 발생하였습니다.");
        }
    }


//    @Override
//    public Map<String, Object> updateIssue(Long connectId, String issueKeyOrId, OnPremiseJiraIssueInputDTO onPremiseJiraIssueInputDTO) throws Exception {
//        Map<String, Object> resultMap = new HashMap<>();
//        try {
//            JiraInfoDTO info = jiraInfo.checkInfo(connectId);
//
//            JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
//                                                                             info.getUserId(),
//                                                                             info.getPasswordOrToken());
//
//            FieldsDTO fields = onPremiseJiraIssueInputDTO.getFields();
//            // summary,description,label 수정 가능 이외 값 수정시 오류
//            if (fields.getProject() != null || fields.getIssuetype() != null || fields.getReporter() != null ||
//                    fields.getAssignee() != null || fields.getIssuelinks() != null || fields.getPriority() != null ||
//                    fields.getSubtasks() != null || fields.getStatus() != null || fields.getResolution() != null) {
//                throw new IllegalArgumentException("수정이 허용되지 않은 필드가 포함되어 있습니다.");
//            }
//
//            IssueInputBuilder issueInputBuilder = new IssueInputBuilder();
//
//
//            if (fields.getSummary() != null) { //요약
//                issueInputBuilder.setSummary(fields.getSummary());
//            }
//            if (fields.getDescription() != null) { // 설명
//                issueInputBuilder.setDescription(fields.getDescription());
//            }
//            if (fields.getLabels() != null) { //라벨
//                issueInputBuilder.setFieldValue("labels", fields.getLabels());
//            }
//
//            IssueInput issueInput = issueInputBuilder.build();
//
//            // 이슈 업데이트 실행
//            restClient.getIssueClient().updateIssue(issueKeyOrId, issueInput).claim();
//            resultMap.put("updateStatus", "success");
//            return resultMap;
//        }  catch (JsonMappingException e) { // JSON 관련 오류 처리
//            resultMap.put("updateStatus", "failed");
//            resultMap.put("errorMessage", "Invalid JSON input: " + e.getMessage());
//
//        }  catch (Exception e) { // 업데이트 실패한 경우
//            resultMap.put("updateStatus", "failed");
//            resultMap.put("errorMessage", e.getMessage());
//        }
//        return resultMap;
//    }

//    @Override
//    public Map<String, Object> deleteIssue(Long connectId, String issueKey) throws Exception {
//        JiraInfoDTO info = jiraInfo.loadConnectInfo(connectId);
//        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
//                                                                         info.getUserId(),
//                                                                         info.getPasswordOrToken());
//
//        // 서브 테스크가 있는지 체크
//        // 서브 테스크가 있다면 라벨링 및 이슈 닫기
//        // 서브 테스크가 없다면 삭제
//        Map<String, Object> result = new HashMap<String, Object>();
//        Issue issue = getIssue(connectId, issueKey);
//        String closeIssue = "close issue";
//
//        // 서브 테스크 존재 여부 체크
//        if (issue.getSubtasks().iterator().hasNext()) { // 서브 테스크 있는 경우
//            logger.info("서브 테스크가 존재합니다.");
//
//            // 라벨링
//            String closedLabel = "closedIssue";
//
//            FieldsDTO fieldsDTO = new FieldsDTO();
//            fieldsDTO.setLabels(List.of(closedLabel));
//
//            OnPremiseJiraIssueInputDTO onPremiseJiraIssueInputDTO = new OnPremiseJiraIssueInputDTO();
//            onPremiseJiraIssueInputDTO.setFields(fieldsDTO);
//
//            Map<String, Object> addLabel = updateIssue(connectId, issueKey, onPremiseJiraIssueInputDTO);
//            if ("success".equals(addLabel.get("updateStatus"))) {
//                result.put("add label success", "라벨링 성공");
//                logger.info("서브 테스크가 존재하는 이슈 라벨링에 성공하였습니다.");
//            } else {
//                result.put("add label fail", "라벨링 실패");
//                logger.info("서브 테스크가 존재하는 이슈 라벨링에 실패하였습니다.");
//            }
//
//            // 이슈 닫기
//            List<Transition> transitions = (List<Transition>) restClient.getIssueClient().getTransitions(issue).claim();
//            Transition closeTransition = transitions.stream()
//                    .filter(transition -> transition.getName().equalsIgnoreCase(closeIssue))
//                    .findFirst()
//                    .orElse(null);
//
//            if (closeTransition != null) {
//                try {
//                    TransitionInput transitionInput = new TransitionInput(closeTransition.getId());
//                    restClient.getIssueClient().transition(issue, transitionInput).claim();
//                    result.put("close issue success", "이슈 닫기 성공");
//                    logger.info("서브 테스크가 존재하는 이슈 닫기에 성공하였습니다.");
//                } catch (Exception e) {
//                    result.put("close issue fail", "이슈 닫기 실패");
//                    logger.info("서브 테스크가 존재하는 이슈 닫기에 실패하였습니다.");
//                }
//            }
//        } else {
//            logger.info("서브 테스크가 존재하지 않습니다.");
//
//            boolean deleteSubtasks = false;
//            try {
//                restClient.getIssueClient().deleteIssue(issueKey, deleteSubtasks).claim();
//                result.put("delete issue success", "이슈 삭제 성공");
//                logger.info("서브 테스크가 존재하지 않는 이슈를 삭제하였습니다.");
//            } catch (Exception e) {
//                result.put("delete issue fail", "이슈 삭제 실패");
//                logger.info("서브 테스크가 존재하지 않는 이슈를 삭제에 실패하였습니다.");
//            }
//        }
//
//        return result;
//    }
//
//    @Transactional
//    @Override
//    public Map<String,Object> collectLinkAndSubtask(Long connectId) throws Exception {
//        List<OnPremiseJiraIssueEntity> list = onPremiseJiraIssueJpaRepository.findByOutwardIdAndParentIdisNullAndConnectId(connectId);
//        List<CloudJiraIssueEntity> saveList = new ArrayList<>();
//
//        for (OnPremiseJiraIssueEntity item : list) {
//            Issue issue = getIssue(item.getConnectId(), item.getId());
//            List<IssueLink> issueLinks = (List<IssueLink>) issue.getIssueLinks();
//            if (issueLinks.size() > 0) {
//                OnPremiseJiraIssueDTO saveChildIssueList = fetchLinkedIssues(item.getConnectId(), item.getId());
//                saveLinkedIssues(item.getConnectId(), null, saveChildIssueList, 0);
//            }
//
//            List<Subtask> subtaskList = (List<Subtask>) issue.getSubtasks();
//            if(subtaskList.size() > 0) {
//
//                for(Subtask subtaskItem : subtaskList) {
//                    OnPremiseJiraIssueDTO saveIssueDTO = getIssueByWebClient(item.getConnectId(), subtaskItem.getIssueKey());
//
//                    OnPremiseJiraIssueDTO saveSubtaskChildIssueList = fetchLinkedIssues(connectId, saveIssueDTO.getId());
//                    saveSubtaskLinkedIssues(connectId, item.getId(), saveSubtaskChildIssueList, 0);
//                }
//            }
//
//        }
//
//        Map<String, Object> result = new HashMap<String, Object>();
//        result.put("success", true);
//        result.put("message", "스케줄러 작동 완료되었습니다.");
//
//        return result;
//    }

    private OnPremiseJiraIssueDTO fetchLinkedIssues(Long connectId, String issueKeyOrId) throws Exception {

        OnPremiseJiraIssueDTO onPremiseJiraIssueDTO = getIssueByWebClient(connectId, issueKeyOrId);

        OnPremiseJiraIssueDTO childLinkDTO
                = new OnPremiseJiraIssueDTO(onPremiseJiraIssueDTO.getId(),
                                            onPremiseJiraIssueDTO.getKey(),
                                            onPremiseJiraIssueDTO.getSelf());

        List<com.arms.jira.onpremise.jiraissue.model.FieldsDTO.IssueLink> issueLinks = onPremiseJiraIssueDTO.getFields().getIssuelinks();

        for (com.arms.jira.onpremise.jiraissue.model.FieldsDTO.IssueLink link : issueLinks) {
            if (link != null) {
                if(link.getInwardIssue() != null) {
                    String linkedIssueKeyOrId = link.getInwardIssue().getKey();
                    if (linkedIssueKeyOrId != null) {
                        OnPremiseJiraIssueDTO linkedIssueDTO = fetchLinkedIssues(connectId, linkedIssueKeyOrId);

                        if (linkedIssueDTO != null) {
                            childLinkDTO.getIssues().add(linkedIssueDTO);
                        }
                    }
                }
            }
        }

        return childLinkDTO;
    }

    private void saveLinkedIssues(Long connectId, String outwardId, OnPremiseJiraIssueDTO saveIssueDTO, int depth) {
        String indent = "  ".repeat(depth);

        OnPremiseJiraIssueEntity onPremiseJiraIssueEntity = modelMapper.map(saveIssueDTO, OnPremiseJiraIssueEntity.class);
        onPremiseJiraIssueEntity.setConnectId(connectId);

        if (outwardId != null) {
            onPremiseJiraIssueEntity.setOutwardId(outwardId);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onPremiseJiraIssueEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));
        onPremiseJiraIssueJpaRepository.save(onPremiseJiraIssueEntity);

        for (OnPremiseJiraIssueDTO linkedIssue : saveIssueDTO.getIssues()) {
            saveLinkedIssues(connectId, saveIssueDTO.getId(), linkedIssue, depth + 1);
        }
    }

    private void saveSubtaskLinkedIssues(Long connectId, String parentId, OnPremiseJiraIssueDTO saveIssueDTO, int depth) {
        String indent = "  ".repeat(depth);
        // System.out.println(indent + "Issue: " + issueDTO.getKey());

        OnPremiseJiraIssueEntity onPremiseJiraIssueEntity = modelMapper.map(saveIssueDTO, OnPremiseJiraIssueEntity.class);
        onPremiseJiraIssueEntity.setConnectId(connectId);

        if (parentId != null) {
            onPremiseJiraIssueEntity.setParentId(parentId);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onPremiseJiraIssueEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));

        onPremiseJiraIssueJpaRepository.save(onPremiseJiraIssueEntity);

        for (OnPremiseJiraIssueDTO linkedIssue : saveIssueDTO.getIssues()) {
            saveLinkedIssues(connectId, saveIssueDTO.getId(), linkedIssue, depth + 1);
        }
    }

    public OnPremiseJiraIssueDTO getIssueByWebClient(Long connectId, String issueKeyOrId) throws Exception {

        String endpoint = "/rest/api/2/issue/" + issueKeyOrId;
        JiraInfoDTO jiraInfoDTO = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = createJiraWebClient(jiraInfoDTO.getUri());

        OnPremiseJiraIssueDTO onPremiseJiraIssueDTO = CloudJiraUtils.get(webClient, endpoint, OnPremiseJiraIssueDTO.class).block();

        return onPremiseJiraIssueDTO;
    }

    public static WebClient createJiraWebClient(String uri) {

        return WebClient.builder()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


}
