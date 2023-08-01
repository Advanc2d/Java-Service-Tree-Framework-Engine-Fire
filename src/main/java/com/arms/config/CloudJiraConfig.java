package com.arms.config;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoDTO;
import com.arms.cloud.jiraconnectinfo.service.CloudJiraConnectInfo;

@Configuration
@DependsOn("cloudJiraConnectInfo")
public class CloudJiraConfig {

    @Value("${cloud.jira.id}")
    public String jiraID;

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
        CloudJiraConnectInfoDTO cloudJiraConnectInfoDTO = cloudJiraConnectInfo.loadConnectInfo(jiraID);

        if(cloudJiraConnectInfoDTO == null || cloudJiraConnectInfoDTO.getUrl().isEmpty() 
                    || cloudJiraConnectInfoDTO.getId().isEmpty()|| cloudJiraConnectInfoDTO.getToken().isEmpty() ) {

            // 오류 처리 필요
            return null;
        }

        return WebClient.builder()
                .baseUrl(cloudJiraConnectInfoDTO.getUrl())
                .defaultHeader("Authorization", "Basic " + getBase64Credentials(cloudJiraConnectInfoDTO.getId(), cloudJiraConnectInfoDTO.getToken()))
                .build();
    }

    private String getBase64Credentials(String jiraID, String jiraPass) {
        String credentials = jiraID + ":" + jiraPass;
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }
}
