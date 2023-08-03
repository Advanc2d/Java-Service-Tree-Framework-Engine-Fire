package com.arms.cloud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoDTO;
import com.arms.cloud.jiraconnectinfo.service.CloudJiraConnectInfo;
import com.arms.cloud.jiraproject.domain.CloudJiraProjectDTO;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CloudJiraConnectInfoTest {

    @Autowired
    private CloudJiraConnectInfo cloudJiraConnectInfo;

    @Test
    @DisplayName("Cloud Jira Connect Info Load Test")
    public void cloudJiraConnectInfoGetTest() {

        CloudJiraConnectInfoDTO found = cloudJiraConnectInfo.loadConnectInfo("1");
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getEmail(), found.getToken());

        CloudJiraProjectDTO cloudJiraProjectDTO 
                        = CloudJiraUtils.get(webClient, 
                                        "/rest/api/3/project/ADVANC2D", 
                                        CloudJiraProjectDTO.class).block();

        assertThat(cloudJiraProjectDTO.getId()).isEqualTo("10000");
    }
}
