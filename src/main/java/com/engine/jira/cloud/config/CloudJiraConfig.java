package com.engine.jira.cloud.config;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.reactive.function.client.WebClient;

import com.engine.jira.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoDTO;
import com.engine.jira.cloud.jiraconnectinfo.service.CloudJiraConnectInfo;

@Configuration
@DependsOn("cloudJiraConnectInfo")
public class CloudJiraConfig {

    @Value("${cloud.oauth2.client.clientId}") 
    public String clientId;

    @Value("${cloud.oauth2.client.clientSecret}") 
    public String clientSecret;

    @Value("${cloud.oauth2.client.accessTokenUri}") 
    public String accessTokenUri;

    @Value("${cloud.oauth2.client.redirectUri}") 
    public String redirectUri;

    @Value("${cloud.oauth2.client.apiResourceUri}") 
    public String apiResourceUri;

    @Value("${cloud.oauth2.client.grantType}") 
    public String grantType;

    @Value("${app.jiraUrl}")
    public String jiraApiUrl;

    @Autowired
    private CloudJiraConnectInfo cloudJiraConnectInfo;

    @Bean
    public WebClient getJiraWebClient() {
        CloudJiraConnectInfoDTO cloudJiraConnectInfoDTO = cloudJiraConnectInfo.loadConnectInfo("1");

        if(cloudJiraConnectInfoDTO == null || cloudJiraConnectInfoDTO.getUri().isEmpty() 
                    || cloudJiraConnectInfoDTO.getEmail().isEmpty()|| cloudJiraConnectInfoDTO.getToken().isEmpty() ) {

            // 오류 처리 필요
            return null;
        }

        return WebClient.builder()
                .baseUrl(cloudJiraConnectInfoDTO.getUri())
                .defaultHeader("Authorization", "Basic " + getBase64Credentials(cloudJiraConnectInfoDTO.getEmail(), cloudJiraConnectInfoDTO.getToken()))
                .build();
    }

    private String getBase64Credentials(String jiraID, String jiraPass) {
        String credentials = jiraID + ":" + jiraPass;
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }
}
