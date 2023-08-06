//package com.engine.jira.cloud;
//
//import com.engine.jira.cloud.jiraproject.model.CloudJiraProjectDTO;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import com.engine.jira.cloud.jiraconnectinfo.model.CloudJiraConnectInfoDTO;
//import com.engine.jira.cloud.jiraconnectinfo.service.CloudJiraConnectInfo;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//public class CloudJiraConnectInfoTest {
//
//    @Autowired
//    private CloudJiraConnectInfo cloudJiraConnectInfo;
//
//    @Test
//    @DisplayName("Cloud Jira Connect Info Load Test")
//    public void cloudJiraConnectInfoGetTest() {
//
//        CloudJiraConnectInfoDTO found = cloudJiraConnectInfo.loadConnectInfo("1");
//        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getEmail(), found.getToken());
//
//        CloudJiraProjectDTO cloudJiraProjectDTO
//                        = CloudJiraUtils.get(webClient,
//                                        "/rest/api/3/project/ADVANC2D",
//                                        CloudJiraProjectDTO.class).block();
//
//        assertThat(cloudJiraProjectDTO.getId()).isEqualTo("10000");
//    }
//}
