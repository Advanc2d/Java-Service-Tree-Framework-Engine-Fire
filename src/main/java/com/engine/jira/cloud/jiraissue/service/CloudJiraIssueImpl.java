package com.engine.jira.cloud.jiraissue.service;

import com.engine.jira.cloud.config.CloudJiraConfig;
import com.engine.jira.cloud.jiraissue.dao.CloudJiraIssueJpaRepository;
import com.engine.jira.cloud.jiraissue.model.CloudJiraIssueDTO;
import com.engine.jira.cloud.jiraissue.model.CloudJiraIssueEntity;
import com.engine.jira.cloud.jiraissue.model.CloudJiraIssueInputDTO;
import com.engine.jira.cloud.jiraissue.model.CloudJiraIssueSearchDTO;
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
    public String deleteIssueAndSubtask(String issueKeyOrId) {
        Optional<CloudJiraIssueEntity> result = cloudJiraIssueJpaRepository.findById(issueKeyOrId);

        if(result.isPresent()){ //디비에서 조회한 값이 있을 때
            String endpoint = "/rest/api/3/issue/" + issueKeyOrId +"?deleteSubtasks=true";
            final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();
            try {
                //지라 서버 삭제 요청
                jiraWebClient.delete()
                        .uri(endpoint)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
                //디비 해당 값 삭제 처리
                cloudJiraIssueJpaRepository.deleteById(issueKeyOrId);
            }catch (WebClientException e){
                String errorMessage = e.getMessage();
                logger.info(errorMessage);
            }catch (Exception e){
                return "삭제 시 오류 발생";
            }
            return "삭제 완료";
        }else{
            return "삭제 처리할 대상이 없음";
        }
    }

    // 서브테스크 있을 시 삭제 안하고 오류 발생 시키는 버전
    @Transactional
    @Override
    public String deleteIssue(String issueKeyOrId) throws JSONException {
        Optional<CloudJiraIssueEntity> result = cloudJiraIssueJpaRepository.findById(issueKeyOrId);
        if(result.isPresent()){
            String endpoint = "/rest/api/3/issue/" + issueKeyOrId ;
            final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();
            try {
                jiraWebClient.delete()
                        .uri(endpoint)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
                return "삭제 완료";
            } catch (WebClientResponseException e) {
                if (e.getRawStatusCode() == 400) {
                    String responseBody = e.getResponseBodyAsString();

                    JSONObject json = new JSONObject(responseBody);
                    String errorMessage = json.getJSONArray("errorMessages").getString(0);

                    return errorMessage;
                } else {
                    throw e;
                }
            }
        }
        else{
            return "삭제 처리할 대상이 없음";
        }

    }

}
