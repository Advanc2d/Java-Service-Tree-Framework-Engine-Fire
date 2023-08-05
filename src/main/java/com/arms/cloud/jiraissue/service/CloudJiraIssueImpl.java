package com.arms.cloud.jiraissue.service;

import com.arms.cloud.jiraissue.dao.CloudJiraIssueJpaRepository;
import com.arms.cloud.jiraissue.domain.*;
import com.arms.config.CloudJiraConfig;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
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
    public void deleteIssue(String issueKeyOrId) throws JSONException{

        Optional<CloudJiraIssueEntity> result = cloudJiraIssueJpaRepository.findById(issueKeyOrId);
        if(result.isPresent()){
            deleteSubTask(issueKeyOrId);
            String endpoint = "/rest/api/3/issue/" + issueKeyOrId +"?deleteSubtasks=false";
            final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();
            jiraWebClient.delete()
                    .uri(endpoint)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            // 디비에서 삭제 처리
            cloudJiraIssueJpaRepository.deleteById(issueKeyOrId);
        }

    }
    @Transactional
    public void deleteSubTask(String issueKeyOrId) throws JSONException {
        if(checkSubTask(issueKeyOrId)){// 이슈에 서브테스크가 있다면
            //이슈에있는 서브테스크 삭제

            // 서브테스크 이슈타입 중 "subtask" false로 변경
            editSubTaskField(issueKeyOrId);
            //  서브테스크의 이슈 아이디 등 정보를 가져와 디비에 저장

        }
    }

    public List<CloudJiraIssueDTO> getSubTask(String issueKeyOrId){
        List<CloudJiraIssueDTO> SubTaskList = getIssue(issueKeyOrId).getFields().getSubtasks();
        return SubTaskList;
    }

    public boolean checkSubTask(String issueKeyOrId){
        if(getSubTask(issueKeyOrId).size()>0){
            return true;
        }else return false;
    }

    // 서브 테스크의 이슈 아이디를 조회하여 필드의 이슈타입 값 중 서브테스크 값을 true 에서 false로 변경 처리
    // 필드 내부 페어런트 값도 삭제 처리
    // 고민 해야할 것 : 부모 이슈 필드의 서브테스크 항목 값을 삭제 처리 해줘야하나??
    public void editSubTaskField(String issueKeyOrId){
        for(int i=0;i<getSubTask(issueKeyOrId).size();i++){
            //서브테스크 이슈 아이디 조회하기
            String id = getSubTask(issueKeyOrId).get(i).getId();
            // "subtask:true ==> subtask:false"로 수정하기, 필드 내부 페어런트 key 삭제 처리 필요 할듯
            JsonNodeFactory jnf = JsonNodeFactory.instance;
            ObjectNode payload = jnf.objectNode();
            ObjectNode fields = payload.putObject("fields");
            fields.remove("parent"); // parent 값 삭제

            ObjectNode issuetype = fields.putObject("issuetype");
            issuetype.put("subtask", false);// 서브테스크 값 변경


            String endpoint = "/rest/api/3/issue/" +id;
            final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();
            String result = jiraWebClient.put()
                    .uri(endpoint)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("@@@@@@@@@"+result);
        }
    }

}
