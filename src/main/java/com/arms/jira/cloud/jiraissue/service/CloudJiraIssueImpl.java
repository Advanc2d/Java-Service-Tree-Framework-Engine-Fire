package com.arms.jira.cloud.jiraissue.service;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraissue.dao.CloudJiraIssueJpaRepository;
import com.arms.jira.cloud.jiraissue.model.*;
import com.arms.jira.cloud.jiraissue.model.PrioritySearchDTO.Priority;
import com.arms.jira.cloud.jiraissue.model.ResolutionSearchDTO.Resolution;
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
    public CloudJiraIssueSearchDTO getIssueSearch(Long connectId, String projectKeyOrId) {

        int startAt = 0;
        int maxResults = 10;
        boolean isLast = false;

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        CloudJiraIssueSearchDTO cloudJiraIssueSearchDTO = new CloudJiraIssueSearchDTO();
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
    public CloudJiraIssueDTO getIssue(Long connectId, String issueKeyOrId) {
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
    public CloudJiraIssueDTO createIssue(Long connectId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception {

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
    public Map<String,Object> updateIssue(Long connectId, String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO) {


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
    public Map<String,Object> deleteIssue(Long connectId, String issueKeyOrId) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();

        String endpoint = "/rest/api/3/issue/" + issueKeyOrId +"?deleteSubtasks=false";
        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        if (checkSubTask(connectId, issueKeyOrId)){ //서브테스크가 있을 경유
//            for (int i=0;i<getSubTask(connectId, issueKeyOrId).size();i++) {
//                convertSubtaskToIssue(connectId, String.valueOf(getSubTask(connectId, issueKeyOrId).get(i).getId()), issueKeyOrId);
//            }

            /* ***
                반장님에게 확인 후 수정사항 (Label 덮어씌기로 반영됨)
            *** */
            String closedLabel = "closeLabel";

            FieldsDTO fieldsDTO = new FieldsDTO();
            fieldsDTO.setLabels(List.of(closedLabel));

            CloudJiraIssueInputDTO cloudJiraIssueInputDTO = new CloudJiraIssueInputDTO();
            cloudJiraIssueInputDTO.setFields(fieldsDTO);

            Map<String, Object> addLabelResult = updateIssue(connectId,issueKeyOrId, cloudJiraIssueInputDTO);

            if (!((Boolean) addLabelResult.get("success"))) {
                result.put("success", false);
                result.put("message", "이슈 라벨 추가 실패");
                
                return result;
            }

            /* ***
                반장님에게 확인 후 수정사항(서브테스크 있을 시 닫기로 변경해야하는데 그 값은 모두 다름)
            *** */
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

    public List<CloudJiraIssueDTO> getSubTask(Long connectId, String issueId){
        List<CloudJiraIssueDTO> SubTaskList = getIssue(connectId, issueId).getFields().getSubtasks();
        return SubTaskList;
    }

    public boolean checkSubTask(Long connectId,String issueKeyOrId) {
        if (getSubTask(connectId, issueKeyOrId).size()>0) {
            return true;
        } else return false;
    }

    /* ***
    * 현재 미사용
    *** */
    public void convertSubtaskToIssue(Long connectId, String subTaskKeyOrId,String issueKeyOrId) throws Exception {

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
    public Map<String,Object> collectLinkAndSubtask(Long connectId) {
        List<CloudJiraIssueEntity> list = cloudJiraIssueJpaRepository.findByOutwardIdAndParentIdisNullAndConnectId(connectId);
        List<CloudJiraIssueEntity> saveList = new ArrayList<>();

        for (CloudJiraIssueEntity item : list) {
            CloudJiraIssueDTO cloudJiraIssueDTO = getIssue(item.getConnectId(), item.getId());

            if (cloudJiraIssueDTO.getFields().getIssuelinks().size() > 0) {
                CloudJiraIssueDTO saveChildIssueList = fetchLinkedIssues(item.getConnectId(), item.getId());
                saveLinkedIssues(item.getConnectId(), null, saveChildIssueList, 0);
            }

            if(cloudJiraIssueDTO.getFields().getSubtasks().size() > 0) {
                List<CloudJiraIssueDTO> subtaskList = cloudJiraIssueDTO.getFields().getSubtasks();

                for(CloudJiraIssueDTO subtaskItem : subtaskList) {
                    CloudJiraIssueDTO saveIssueDTO = getIssue(item.getConnectId(), subtaskItem.getId());

                    CloudJiraIssueDTO saveSubtaskChildIssueList = fetchLinkedIssues(connectId, saveIssueDTO.getId());
                    saveSubtaskLinkedIssues(connectId, item.getId(), saveSubtaskChildIssueList, 0);

                }
            }
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("message", "스케줄러 작동 완료되었습니다.");

        return result;
    }

    @Transactional
    public Map<String,Object> collectLinkAndSubtaskByIssueTypeName(Long connectId) {

        CloudJiraIssueSearchDTO cloudJiraIssueSearchDTO = getIssueListByIssueTypeName(connectId, "요구사항");
        List<CloudJiraIssueDTO> cloudJiraIssueList = cloudJiraIssueSearchDTO.getIssues();

        List<CloudJiraIssueEntity> saveList = new ArrayList<>();

        for (CloudJiraIssueDTO item : cloudJiraIssueList) {
            CloudJiraIssueDTO cloudJiraIssueDTO = getIssue(connectId, item.getId());

            if (cloudJiraIssueDTO.getFields().getIssuelinks().size() > 0) {
                  CloudJiraIssueDTO saveChildIssueList = fetchLinkedIssues(connectId, item.getId());
                  saveLinkedIssues(connectId, null, saveChildIssueList, 0);
            }

            if(cloudJiraIssueDTO.getFields().getSubtasks().size() > 0) {
                List<CloudJiraIssueDTO> subtaskList = cloudJiraIssueDTO.getFields().getSubtasks();

                for(CloudJiraIssueDTO subtaskItem : subtaskList) {
                    CloudJiraIssueDTO saveIssueDTO = getIssue(connectId, subtaskItem.getId());

                    CloudJiraIssueDTO saveSubtaskChildIssueList = fetchLinkedIssues(connectId, saveIssueDTO.getId());
                    saveSubtaskLinkedIssues(connectId, subtaskItem.getId(), saveSubtaskChildIssueList, 0);

                }
            }

            if (saveList.size() > 100) {
                cloudJiraIssueJpaRepository.saveAll(saveList);
            }
        }

        if (saveList.size() > 0 && saveList.size() <= 100) {
            cloudJiraIssueJpaRepository.saveAll(saveList);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("message", "스케줄러 작동 완료되었습니다.");

        return result;
    }

    public CloudJiraIssueSearchDTO getIssueListByIssueTypeName(Long connectId, String issueTypName) {
        int startAt = 0;
        int maxResults = 10;
        boolean isLast = false;

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        CloudJiraIssueSearchDTO cloudJiraIssueSearchDTO = new CloudJiraIssueSearchDTO();
        List<CloudJiraIssueDTO> issueList = new ArrayList<>(); // 이슈 저장

        while (!isLast) {
            String endpoint = "/rest/api/3/search?jql=issuetype=" + issueTypName + "&startAt=" + startAt + "&maxResults=" + maxResults;
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

    private CloudJiraIssueDTO fetchLinkedIssues(Long connectId, String issueKeyOrId) {

        CloudJiraIssueDTO cloudJiraIssueDTO = getIssue(connectId, issueKeyOrId);

        CloudJiraIssueDTO childLinkDTO = new CloudJiraIssueDTO(cloudJiraIssueDTO.getId(),
                                                        cloudJiraIssueDTO.getKey(), cloudJiraIssueDTO.getSelf());

        List<FieldsDTO.IssueLink> issueLinks = cloudJiraIssueDTO.getFields().getIssuelinks();

        for (FieldsDTO.IssueLink link : issueLinks) {

            if (link.getInwardIssue() != null) {
                String linkedIssueKey = link.getInwardIssue().getKey();
                CloudJiraIssueDTO linkedIssueDTO = fetchLinkedIssues(connectId, linkedIssueKey);

                if (linkedIssueDTO != null) {
                    childLinkDTO.getIssues().add(linkedIssueDTO);
                }
            }
        }

        return childLinkDTO;
    }

    private void saveLinkedIssues(Long connectId, String outwardId, CloudJiraIssueDTO saveIssueDTO, int depth) {
        String indent = "  ".repeat(depth);

        CloudJiraIssueEntity cloudJiraIssueEntity = modelMapper.map(saveIssueDTO, CloudJiraIssueEntity.class);
        cloudJiraIssueEntity.setConnectId(connectId);

        if (outwardId != null) {
            cloudJiraIssueEntity.setOutwardId(outwardId);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        cloudJiraIssueEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));
        cloudJiraIssueJpaRepository.save(cloudJiraIssueEntity);

        for (CloudJiraIssueDTO linkedIssue : saveIssueDTO.getIssues()) {
            saveLinkedIssues(connectId, saveIssueDTO.getId(), linkedIssue, depth + 1);
        }
    }

    private void saveSubtaskLinkedIssues(Long connectId, String parentId, CloudJiraIssueDTO saveIssueDTO, int depth) {
        String indent = "  ".repeat(depth);

        CloudJiraIssueEntity cloudJiraIssueEntity = modelMapper.map(saveIssueDTO, CloudJiraIssueEntity.class);
        cloudJiraIssueEntity.setConnectId(connectId);

        if (parentId != null) {
            cloudJiraIssueEntity.setParentId(parentId);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        cloudJiraIssueEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));

        cloudJiraIssueJpaRepository.save(cloudJiraIssueEntity);

        for (CloudJiraIssueDTO linkedIssue : saveIssueDTO.getIssues()) {
            saveLinkedIssues(connectId, saveIssueDTO.getId(), linkedIssue, depth + 1);
        }
    }

    public TransitionsDTO getIssueStatusAll(Long connectId, String issueKeyOrId) {

        String endpoint = "/rest/api/3/issue/" + issueKeyOrId +"/transitions";

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        TransitionsDTO transitions = CloudJiraUtils.get(webClient, endpoint, TransitionsDTO.class).block();

        return transitions;
    }

    public Map<String,Object> updateIssueStatus(Long connectId, String issueKeyOrId,
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

    @Override
    public PrioritySearchDTO getPriorityList(Long connectId) {

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        int maxResult = 1048576;
        int startAt = 0;
        int index= 1;
        boolean checkLast = false;

        List<Priority> values = new ArrayList<Priority>();
        PrioritySearchDTO result = null;

        while(!checkLast) {
            String endpoint = "/rest/api/3/priority/search?maxResults="+ maxResult + "&startAt=" + startAt;
            PrioritySearchDTO priorities = CloudJiraUtils.get(webClient, endpoint, PrioritySearchDTO.class).block();

            values.addAll(priorities.getValues());

            if (priorities.getTotal() == values.size()) {
                result = priorities;
                result.setValues(null);

                checkLast = true;
            }
            else {
                startAt = maxResult * index;
                index++;
            }
        }

        result.setValues(values);

        return result;
    }

    @Override
    public ResolutionSearchDTO getResolutionList(Long connectId) {

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        int maxResult = 1048576;
        int startAt = 0;
        int index= 1;
        boolean checkLast = false;

        List<Resolution> values = new ArrayList<Resolution>();
        ResolutionSearchDTO result = null;

        while(!checkLast) {
            String endpoint = "/rest/api/3/resolution/search?maxResults="+ maxResult + "&startAt=" + startAt;
            ResolutionSearchDTO resolutions = CloudJiraUtils.get(webClient, endpoint, ResolutionSearchDTO.class).block();

            values.addAll(resolutions.getValues());

            if (resolutions.getTotal() == values.size()) {
                result = resolutions;
                result.setValues(null);

                checkLast = true;
            }
            else {
                startAt = maxResult * index;
                index++;
            }
        }

        result.setValues(values);

        return result;
    }
}
