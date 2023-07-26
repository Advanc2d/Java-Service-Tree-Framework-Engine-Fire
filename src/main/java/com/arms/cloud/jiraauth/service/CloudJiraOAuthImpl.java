package com.arms.cloud.jiraauth.service;

import com.arms.cloud.jiraauth.domain.CloudJiraTokenRequestDTO;
import com.arms.config.CloudJiraConfig;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@Service("cloudJiraOAuth")
public class CloudJiraOAuthImpl implements CloudJiraOAuth {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String autorizationUrl= "https://auth.atlassian.com/authorize?audience=api.atlassian.com&client_id=h3G2k7xZbgBt5odGcGvKrqYhhIvtFTLh&scope=read%3Ajira-work%20manage%3Ajira-project%20manage%3Ajira-configuration%20read%3Ajira-user%20write%3Ajira-work%20manage%3Ajira-webhook%20manage%3Ajira-data-provider&redirect_uri=http%3A%2F%2Flocalhost%3A31313%2Fcloud%2Fjira%2Foauth%2Fcallback&state=${YOUR_USER_BOUND_VALUE}&response_type=code&prompt=consent";

    @Autowired
    public CloudJiraConfig cloudJiraConfig;

    public ResponseEntity cloudJiraAuthorization() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = restTemplate.exchange(autorizationUrl, HttpMethod.GET, null, String.class);
        
        return response;
    }

    public String cloudJiraAccessToken(String code, HttpServletRequest request) {
        // accesstoken 발급
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders  headers = new HttpHeaders();
        ObjectMapper objectMapper = new ObjectMapper();

        headers.setContentType(MediaType.APPLICATION_JSON);

        CloudJiraTokenRequestDTO requestBody = new CloudJiraTokenRequestDTO();
        requestBody.setGrant_type(cloudJiraConfig.grantType);
        requestBody.setClient_id(cloudJiraConfig.clientId);
        requestBody.setClient_secret(cloudJiraConfig.clientSecret);
        requestBody.setCode(code);
        requestBody.setRedirect_uri(cloudJiraConfig.redirectUri);
        HttpEntity<CloudJiraTokenRequestDTO> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(cloudJiraConfig.accessTokenUri, HttpMethod.POST,
                requestEntity, String.class);

        String response = responseEntity.getBody();
        logger.info(response);

        String accessToken = "";
        String id = "";
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            accessToken = jsonNode.get("access_token").asText();
            logger.info("Access Token :" + accessToken);
            id = cloudJiraCloudId(accessToken);
            /* 결과 값 세션 저장
            HttpSession session = request.getSession(true);
            session.setAttribute("ACCESS_TOKEN", accessToken);
            session.setAttribute("ClOUD_ID", cloudJiraCloudId(accessToken));
            */
        }
        catch (Exception e ) {
            e.getMessage();
        }
        logger.info(cloudJiraConfig.apiResourceUri);

        String uri = cloudJiraConfig.jiraApiUrl + id +  "/rest/api/3/project";

        WebClient jiraClient = WebClient.builder().build();
        String res = jiraClient.get()
                    .uri(uri)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class).block();

        logger.info(res);

        HttpSession session = request.getSession(true);
        session.setAttribute("ACCESS_TOKEN", accessToken);

        return res;
    }

    public String cloudJiraCloudId(String accessToken){
        // Cloud id 조회
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders  headers = new HttpHeaders();
        ObjectMapper objectMapper = new ObjectMapper();

        headers.setBearerAuth(accessToken);
        ResponseEntity<String> responseEntity = restTemplate.exchange(cloudJiraConfig.apiResourceUri, HttpMethod.GET,
                new HttpEntity<>(null, headers), String.class);
        String response = responseEntity.getBody();

        logger.info(response);
        String id = "";
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            ObjectNode objectNode = (ObjectNode) jsonNode.get(0);
            id = objectNode.get("id").asText();

            logger.info("id :" + id);
        }
        catch (Exception e ) {
            e.getMessage();
        }

        return id;
    }
}
