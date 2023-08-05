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

    @Autowired
    private JiraInfo jiraInfo;

    @Bean
    public WebClient getJiraWebClient() {
        JiraInfoDTO jiraInfoDTO = jiraInfo.loadConnectInfo("1");

        if(jiraInfoDTO == null || jiraInfoDTO.getUri().isEmpty()
                    || jiraInfoDTO.getUserId().isEmpty()|| jiraInfoDTO.getPasswordOrToken().isEmpty() ) {

            // 오류 처리 필요
            return null;
        }

//        JiraInfoDTO jiraInfoDTO = new JiraInfoDTO();
//        jiraInfoDTO.setUri("https://advanc2d.atlassian.net");
//        jiraInfoDTO.setUserId("gkfn185@gmail.com");
//        jiraInfoDTO.setPasswordOrToken("ATATT3xFfGF0OhyPJU1DlcjJmtsZBXsuXPmet-VBfz07AN6R_vGsV6rOeO6loKVV7iEBsMsmW0WPO4vpPokpcRR_QMrpHi9VJtWdLDLKrhG27j6aGFCeQh5_0sDjWjK45jcJsmQ606vB2Mt9ZYfSAdrRRjlUHceqBiU_Mq7--spJIpAOy7Wi0w4=0122341F");

        return WebClient.builder()
                .baseUrl(jiraInfoDTO.getUri())
                .defaultHeader("Authorization", "Basic " + getBase64Credentials(jiraInfoDTO.getUserId(),
                                                                            jiraInfoDTO.getPasswordOrToken()))
                .build();
    }

    private String getBase64Credentials(String jiraID, String jiraPass) {
        String credentials = jiraID + ":" + jiraPass;
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }
}
