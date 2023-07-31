package com.arms.config;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CloudJiraConfig {
    @Value("${cloud.jira.baseurl}")
    public String jiraUrl;

    @Value("${cloud.jira.id}")
    public String jiraID;

    @Value("${cloud.jira.pass}")
    public String jiraPass;

    @Value("${cloud.jira.projectKey}")
    public String projectKey;

    @Value("${cloud.jira.apiVersion}")
    public String apiVersion;

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

    @Bean
    public WebClient getJiraWebClient() {
        return WebClient.builder()
                .baseUrl(jiraUrl)
                .defaultHeader("Authorization", "Basic " + getBase64Credentials(jiraID, jiraPass))
                .build();
    }

    private String getBase64Credentials(String jiraID, String jiraPass) {
        String credentials = jiraID + ":" + jiraPass;
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }
}
