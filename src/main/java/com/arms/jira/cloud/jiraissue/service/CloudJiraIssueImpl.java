package com.arms.jira.cloud.jiraissue.service;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraissue.dao.CloudJiraIssueJpaRepository;
import com.arms.jira.cloud.jiraissue.model.*;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Timestamp;
import java.util.*;

@AllArgsConstructor
@Service("cloudJiraIssue")
public class CloudJiraIssueImpl implements CloudJiraIssue {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CloudJiraIssueJpaRepository cloudJiraIssueJpaRepository;

    @Override
    public CloudJiraIssueSearchDTO getIssueSearch(String connectId, String projectKeyOrId) {

        int startAt = 0;
        int maxResults = 10;
        boolean isLast = false;

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        CloudJiraIssueSearchDTO cloudJiraIssueSearchDTO = null;
        List<CloudJiraIssueDTO> issueList = new ArrayList<>(); // 이슈 저장

        while (!isLast) {
            String endpoint = "/rest/api/3/search?jql=project=" + projectKeyOrId + "&startAt=" + startAt + "&maxResults=" + maxResults;
            CloudJiraIssueSearchDTO cloudJiraIssuePaging = CloudJiraUtils.get(webClient, endpoint, CloudJiraIssueSearchDTO.class).block();

            issueList.addAll(cloudJiraIssuePaging.getIssues());

            if (cloudJiraIssuePaging.getTotal() == issueList.size()) {
                isLast = true;
                cloudJiraIssueSearchDTO = cloudJiraIssuePaging;
                cloudJiraIssueSearchDTO.setIssues(issueList);

                return cloudJiraIssueSearchDTO;
            }

            startAt += maxResults;
        }

        return cloudJiraIssueSearchDTO;
    }

    @Override
    public CloudJiraIssueDTO getIssue(String connectId, String issueKeyOrId) {
        String endpoint = "/rest/api/3/issue/" + issueKeyOrId;

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        CloudJiraIssueDTO response = CloudJiraUtils.get(webClient, endpoint, CloudJiraIssueDTO.class).block();

        String jsonResponse = response.toString();
        logger.info(jsonResponse);

        return response;
    }

    @Transactional
    @Override
    public CloudJiraIssueDTO createIssue(String connectId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception {

        String endpoint = "/rest/api/3/issue";

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        CloudJiraIssueDTO response = CloudJiraUtils.post(webClient, endpoint, cloudJiraIssueInputDTO, CloudJiraIssueDTO.class).block();

        String jsonResponse = response.toString();
        logger.info(jsonResponse);

        CloudJiraIssueEntity cloudJiraIssueEntity = modelMapper.map(response,CloudJiraIssueEntity.class);

        cloudJiraIssueEntity.setConnectId(connectId);
        cloudJiraIssueJpaRepository.save(cloudJiraIssueEntity);

        return response;
    }

    @Override
    public Map<String,Object> updateIssue(String connectId, String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO) {


        String endpoint = "/rest/api/3/issue/" + issueKeyOrId;
        HttpStatus statusCode = null;
        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        Optional<Boolean> response = CloudJiraUtils.executePut(webClient, endpoint, cloudJiraIssueInputDTO);

        Map<String,Object> result = new HashMap<String,Object>();

        boolean isSuccess = false;

        if (response.isPresent()) {
            if (response.get()) {
                // PUT 호출이 HTTP 204로 성공했습니다.
                isSuccess = true;
                result.put("success", isSuccess);
                result.put("message", "이슈 수정 성공");

                return result;
            }
        }

        result.put("success", isSuccess);
        result.put("message", "이슈 수정 실패");

        return result;

    }

    @Transactional
    @Override
    public Map<String,Object> deleteIssue(String connectId, String issueKeyOrId) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();

        String endpoint = "/rest/api/3/issue/" + issueKeyOrId +"?deleteSubtasks=false";
        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        if (checkSubTask(connectId, issueKeyOrId)){ //서브테스크가 있을 경유
//            for (int i=0;i<getSubTask(connectId, issueKeyOrId).size();i++) {
//                convertSubtaskToIssue(connectId, String.valueOf(getSubTask(connectId, issueKeyOrId).get(i).getId()), issueKeyOrId);
//            }

            String labelValue = "삭제처리이슈";
            IssueLabelUpdateRequestDTO.Label label = new IssueLabelUpdateRequestDTO.Label();
            label.setAdd(labelValue);

            List<IssueLabelUpdateRequestDTO.Label> labels = new ArrayList<>();
            labels.add(label);

            IssueLabelUpdateRequestDTO.Update update = new IssueLabelUpdateRequestDTO.Update();
            update.setLabels(labels);

            IssueLabelUpdateRequestDTO issueLabelUpdateRequestDTO = new IssueLabelUpdateRequestDTO();
            issueLabelUpdateRequestDTO.setUpdate(update);

            Map<String, Object> addLabelResult = addLabel(connectId,issueKeyOrId, issueLabelUpdateRequestDTO);

            if (!((Boolean) addLabelResult.get("success"))) {
                result.put("success", false);
                result.put("message", "이슈 라벨 추가 실패");
                
                return result;
            }

            String transitionsId = "2";
            IssueStatusUpdateRequestDTO.TransitionInputDTO transitionInputDTO = new IssueStatusUpdateRequestDTO.TransitionInputDTO();
            transitionInputDTO.setId(transitionsId);
        
            IssueStatusUpdateRequestDTO issueStatusUpdateRequestDTO = new IssueStatusUpdateRequestDTO();
            issueStatusUpdateRequestDTO.setTransition(transitionInputDTO);

            Map<String, Object> updateIssueStatusResult = updateIssueStatus(connectId, issueKeyOrId, issueStatusUpdateRequestDTO);

            if (!((Boolean) updateIssueStatusResult.get("success"))) {
                result.put("success", false);
                result.put("message", "이슈 상태 변경 실패");

                return result;
            }

            if ((Boolean) addLabelResult.get("success") && (Boolean) updateIssueStatusResult.get("success")) {
                result.put("success", true);
                result.put("message", "서브테스크 이슈가 있으므로 이슈를 라벨 처리 및 닫기로 상태 변경 완료");

                return result;
            }
        }

        Optional<Boolean> response = CloudJiraUtils.executeDelete(webClient, endpoint);

        boolean isSuccess = false;

        if (response.isPresent()) {
            if (response.get()) {
                // PUT 호출이 HTTP 204로 성공했습니다.
                isSuccess = true;
                result.put("success", isSuccess);
                result.put("message", "이슈 삭제 성공");
                // cloudJiraIssueJpaRepository.deleteById(issueKeyOrId);

                return result;
            }
        }

        result.put("success", isSuccess);
        result.put("message", "이슈 삭제 실패");

        return result;
    }

    public List<CloudJiraIssueDTO> getSubTask(String connectId, String issueId){
        List<CloudJiraIssueDTO> SubTaskList = getIssue(connectId, issueId).getFields().getSubtasks();
        return SubTaskList;
    }

    public boolean checkSubTask(String connectId,String issueKeyOrId) {
        if (getSubTask(connectId, issueKeyOrId).size()>0) {
            return true;
        } else return false;
    }

    public void convertSubtaskToIssue(String connectId, String  subTaskKeyOrId,String issueKeyOrId) throws Exception {

        CloudJiraIssueDTO issue = getIssue(connectId, subTaskKeyOrId);
        String issueTypeId      = getIssue(connectId, issueKeyOrId).getFields().getIssuetype().getId();
        String projectId        = issue.getFields().getProject().getId();
        String summary          = issue.getFields().getSummary();
        FieldsDTO.Description descriptionNode = issue.getFields().getDescription();

        FieldsDTO.Project projectDTO = new FieldsDTO.Project();
        projectDTO.setId(projectId);

        FieldsDTO.IssueType issueTypeDTO = new FieldsDTO.IssueType();
        issueTypeDTO.setId(issueTypeId);

        FieldsDTO fieldsDTO = new FieldsDTO();
        fieldsDTO.setProject(projectDTO);
        fieldsDTO.setIssuetype(issueTypeDTO);
        fieldsDTO.setSummary(summary);
        fieldsDTO.setDescription(descriptionNode);

        CloudJiraIssueInputDTO cloudJiraIssueInputDTO = new CloudJiraIssueInputDTO();
        cloudJiraIssueInputDTO.setFields(fieldsDTO);
        logger.info(cloudJiraIssueInputDTO.toString());
        createIssue(connectId, cloudJiraIssueInputDTO);
        // 이슈 생성하고 기존의 서브테스크를 지우는 방식
        // 이슈 생성하고 서브테스크 상위 이슈를 지우는 방식으로 갈지 고민
    }

    @Transactional
    @Override
    public Map<String,Object> collectLinkAndSubtask(String connectId) {
        List<CloudJiraIssueEntity> list = cloudJiraIssueJpaRepository.findAll();
        List<CloudJiraIssueEntity> saveList = new ArrayList<>();

        for (CloudJiraIssueEntity item : list) {
            CloudJiraIssueDTO cloudJiraIssueDTO = getIssue(item.getConnectId(), item.getId());

            if (cloudJiraIssueDTO.getFields().getIssuelinks().size() > 0) {
                List<FieldsDTO.IssueLink> linkList = cloudJiraIssueDTO.getFields().getIssuelinks();

                for(FieldsDTO.IssueLink linkItem : linkList) {
                    if(linkItem.getInwardIssue() != null) {
                        CloudJiraIssueDTO saveIssueDTO = getIssue(item.getConnectId(), linkItem.getInwardIssue().getId());

                        CloudJiraIssueEntity cloudJiraIssueEntity = modelMapper.map(saveIssueDTO, CloudJiraIssueEntity.class);
                        cloudJiraIssueEntity.setConnectId(item.getConnectId());
                        cloudJiraIssueEntity.setOutwardId(item.getId());
                        cloudJiraIssueEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));

                        saveList.add(cloudJiraIssueEntity);
                        // cloudJiraIssueJpaRepository.save(cloudJiraIssueEntity);
                    }
                }
            }

            if(cloudJiraIssueDTO.getFields().getSubtasks().size() > 0) {
                List<CloudJiraIssueDTO> subtaskList = cloudJiraIssueDTO.getFields().getSubtasks();

                for(CloudJiraIssueDTO subtaskItem : subtaskList) {
                    CloudJiraIssueDTO saveIssueDTO = getIssue(item.getConnectId(), subtaskItem.getId());

                    CloudJiraIssueEntity cloudJiraIssueEntity = modelMapper.map(saveIssueDTO, CloudJiraIssueEntity.class);
                    cloudJiraIssueEntity.setConnectId(item.getConnectId());
                    cloudJiraIssueEntity.setParentId(item.getId());
                    cloudJiraIssueEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));

                    saveList.add(cloudJiraIssueEntity);
                    // cloudJiraIssueJpaRepository.save(cloudJiraIssueEntity);
                }
            }

            if (saveList.size() > 100) {
                cloudJiraIssueJpaRepository.saveAll(saveList);
            }
        }

        if (saveList.size() > 0 && saveList.size() < 100) {
            cloudJiraIssueJpaRepository.saveAll(saveList);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("message", "스케줄러 작동 완료되었습니다.");

        return result;
    }

    public Map<String, Object> addLabel(String connectId, String issueKeyOrId,
                                        IssueLabelUpdateRequestDTO issueLabelUpdateRequestDTO) {

        String endpoint = "/rest/api/3/issue/" + issueKeyOrId;

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

//        IssueLabelUpdateRequestDTO.Label label = new IssueLabelUpdateRequestDTO.Label();
//        label.setAdd(labelValue);
//
//        List<IssueLabelUpdateRequestDTO.Label> labels = new ArrayList<>();
//        labels.add(label);
//
//        IssueLabelUpdateRequestDTO.Update update = new IssueLabelUpdateRequestDTO.Update();
//        update.setLabels(labels);
//
//        IssueLabelUpdateRequestDTO issueLabelUpdateRequestDTO = new IssueLabelUpdateRequestDTO();
//        issueLabelUpdateRequestDTO.setUpdate(update);

        Optional<Boolean> response = CloudJiraUtils.executePut(webClient, endpoint, issueLabelUpdateRequestDTO);

        Map<String,Object> result = new HashMap<String,Object>();

        boolean isSuccess = false;

        if (response.isPresent()) {
            if (response.get()) {
                // PUT 호출이 HTTP 204로 성공했습니다.
                isSuccess = true;
                result.put("success", isSuccess);
                result.put("message", "이슈 라벨 추가 성공");

                return result;
            }
        }

        result.put("success", isSuccess);
        result.put("message", "이슈 라벨 추가 실패");

        return result;
    }

    public TransitionsDTO getIssueStatusAll(String connectId, String issueKeyOrId) {

        String endpoint = "/rest/api/3/issue/" + issueKeyOrId +"/transitions";

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        TransitionsDTO transitions = CloudJiraUtils.get(webClient, endpoint, TransitionsDTO.class).block();

        return transitions;
    }

    public Map<String,Object> updateIssueStatus(String connectId, String issueKeyOrId,
                                                IssueStatusUpdateRequestDTO issueStatusUpdateRequestDTO) {

        String endpoint = "/rest/api/3/issue/" + issueKeyOrId +"/transitions";

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

//        IssueStatusUpdateRequestDTO.TransitionInputDTO transitionInputDTO = new IssueStatusUpdateRequestDTO.TransitionInputDTO();
//        transitionInputDTO.setId(transitionsId);
//
//        IssueStatusUpdateRequestDTO issueStatusUpdateRequestDTO = new IssueStatusUpdateRequestDTO();
//        issueStatusUpdateRequestDTO.setTransition(transitionInputDTO);

        Optional<Boolean> response = CloudJiraUtils.executePost(webClient, endpoint, issueStatusUpdateRequestDTO);

        Map<String,Object> result = new HashMap<String,Object>();

        boolean isSuccess = false;

        if (response.isPresent()) {
            if (response.get()) {
                // PUT 호출이 HTTP 204로 성공했습니다.
                isSuccess = true;
                result.put("success", isSuccess);
                result.put("message", "이슈 상태 수정 성공");

                return result;
            }
        }

        result.put("success", isSuccess);
        result.put("message", "이슈 상태 수정 실패");

        return result;
    }
}