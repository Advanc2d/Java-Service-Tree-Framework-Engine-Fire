package com.arms.cloud.jiraissue.service;

import com.arms.cloud.jiraissue.dao.CloudJiraIssueJpaRepository;
import com.arms.cloud.jiraissue.domain.*;
import com.arms.config.CloudJiraConfig;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service("cloudJiraIssue")
public class CloudJiraIssueImpl implements CloudJiraIssue {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("cloudJiraConfig")
    private CloudJiraConfig cloudJiraConfig;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CloudJiraIssueJpaRepository cloudJiraIssueJpaRepository;

    @Override
    public CloudJiraIssueSearchDTO getIssueSearch(String projectKeyOrId) {
        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

        String endpoint = "/rest/api/3/search?jql=project=" + projectKeyOrId;

        CloudJiraIssueSearchDTO response = jiraWebClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(CloudJiraIssueSearchDTO.class)
                .block();

        String jsonResponse = response.toString();
        logger.info(jsonResponse);

        return response;
    }

    @Override
    public CloudJiraIssueDTO getIssue(String issueKeyOrId) {
        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

        String endpoint = "/rest/api/3/issue/" + issueKeyOrId;

        CloudJiraIssueDTO response = jiraWebClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(CloudJiraIssueDTO.class)
                .block();

        String jsonResponse = response.toString();
        logger.info(jsonResponse);

        return response;
    }

    @Transactional
    @Override
    public CloudJiraIssueDTO createIssue(CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception {

        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

        String endpoint = "/rest/api/3/issue";

        CloudJiraIssueDTO response = jiraWebClient.post()
                .uri(endpoint)
                .bodyValue(cloudJiraIssueInputDTO)
                .retrieve()
                .bodyToMono(CloudJiraIssueDTO.class)
                .block();

        String jsonResponse = response.toString();
        logger.info(jsonResponse);

        CloudJiraIssueEntity cloudJiraIssueEntity = modelMapper.map(response,CloudJiraIssueEntity.class);
        cloudJiraIssueJpaRepository.save(cloudJiraIssueEntity);

        return response;
    }

    @Override
    public String updateIssue(String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO) {

        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

        String endpoint = "/rest/api/3/issue/" + issueKeyOrId;
        HttpStatus statusCode = null;

        try {
             WebClient.ResponseSpec responseSpec = jiraWebClient.put()
                    .uri(endpoint)
                    .bodyValue(cloudJiraIssueInputDTO)
                    .retrieve();

             statusCode = responseSpec.toBodilessEntity().block().getStatusCode();

             logger.info("응답 상태 코드: " + statusCode);

             if (statusCode.is2xxSuccessful()) {
                 logger.info("이슈가 성공적으로 수정되었습니다.");
             } else {
                 logger.info("이슈 수정에 실패하였습니다.");
             }
        } catch (WebClientResponseException ex) {
            logger.error("API 호출 실패: " + ex.getStatusCode() + " " + ex.getStatusText());
        }

        String status = statusCode.toString();

        return status;
    }

    // 서브테스크 무조건 삭제 하는 버전
    @Transactional
    @Override
    public void deleteIssue(String issueKeyOrId) throws Exception {

        String endpoint ="";
        if(checkSubTask(issueKeyOrId)){ //서브테스크가 있을 경유
            for(int i=0;i<getSubTask(issueKeyOrId).size();i++){
                convertSubtaskToIssue(String.valueOf(getSubTask(issueKeyOrId).get(i).getId()), issueKeyOrId);
            }
            endpoint= "/rest/api/3/issue/" + issueKeyOrId +"?deleteSubtasks=true";
        }else{
            endpoint = "/rest/api/3/issue/" + issueKeyOrId +"?deleteSubtasks=false";
        }
        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();
        jiraWebClient.delete()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        cloudJiraIssueJpaRepository.deleteById(issueKeyOrId);

    }

    public List<CloudJiraIssueDTO> getSubTask(String issueId){
        List<CloudJiraIssueDTO> SubTaskList = getIssue(issueId).getFields().getSubtasks();
        return SubTaskList;
    }

    public boolean checkSubTask(String issueKeyOrId){
        if(getSubTask(issueKeyOrId).size()>0){
            return true;
        }else return false;
    }

    public void convertSubtaskToIssue(String  subTaskKeyOrId,String issueKeyOrId) throws Exception {

        CloudJiraIssueDTO issue = getIssue(subTaskKeyOrId);
        String issueTypeId      = getIssue(issueKeyOrId).getFields().getIssuetype().getId();
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
        createIssue(cloudJiraIssueInputDTO);
        // 이슈 생성하고 기존의 서브테스크를 지우는 방식
        // 이슈 생성하고 서브테스크 상위 이슈를 지우는 방식으로 갈지 고민
    }
}
