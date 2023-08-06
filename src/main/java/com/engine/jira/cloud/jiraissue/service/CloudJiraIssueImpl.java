package com.engine.jira.cloud.jiraissue.service;

import com.engine.jira.cloud.CloudJiraUtils;
import com.engine.jira.cloud.jiraissue.dao.CloudJiraIssueJpaRepository;
import com.engine.jira.cloud.jiraissue.model.*;
import com.engine.jira.info.model.JiraInfoDTO;
import com.engine.jira.info.service.JiraInfo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        String endpoint = "/rest/api/3/search?jql=project=" + projectKeyOrId;

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        CloudJiraIssueSearchDTO response = CloudJiraUtils.get(webClient, endpoint, CloudJiraIssueSearchDTO.class).block();

        String jsonResponse = response.toString();
        logger.info(jsonResponse);

        return response;
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
}
