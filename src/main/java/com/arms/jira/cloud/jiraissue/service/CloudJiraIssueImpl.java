package com.arms.jira.cloud.jiraissue.service;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraissue.model.*;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.cloud.jiraissue.dao.CloudJiraIssueJpaRepository;
import com.arms.jira.cloud.jiraissue.model.*;
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

        Optional<Boolean> response = CloudJiraUtils.executePut(webClient, cloudJiraIssueInputDTO, endpoint);

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
    public void deleteIssue(String connectId, String issueKeyOrId) throws Exception {

        String endpoint ="";
        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        if(checkSubTask(connectId, issueKeyOrId)){ //서브테스크가 있을 경유
            for(int i=0;i<getSubTask(connectId, issueKeyOrId).size();i++){
                convertSubtaskToIssue(connectId, String.valueOf(getSubTask(connectId, issueKeyOrId).get(i).getId()), issueKeyOrId);
            }
            endpoint= "/rest/api/3/issue/" + issueKeyOrId +"?deleteSubtasks=true";
        }else{
            endpoint = "/rest/api/3/issue/" + issueKeyOrId +"?deleteSubtasks=false";
        }

        CloudJiraUtils.delete(webClient, endpoint, Void.class).block();

        cloudJiraIssueJpaRepository.deleteById(issueKeyOrId);

    }

    public List<CloudJiraIssueDTO> getSubTask(String connectId, String issueId){
        List<CloudJiraIssueDTO> SubTaskList = getIssue(connectId, issueId).getFields().getSubtasks();
        return SubTaskList;
    }

    public boolean checkSubTask(String connectId,String issueKeyOrId){
        if(getSubTask(connectId, issueKeyOrId).size()>0){
            return true;
        }else return false;
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
    public String collectLinkAndSubtask(String connectId) {
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

        return "success";
    }
}
