package com.engine.jira.cloud.config;

import java.util.Base64;

import com.engine.jira.info.model.JiraInfoDTO;
import com.engine.jira.info.service.JiraInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@DependsOn("jiraInfo")
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

}
