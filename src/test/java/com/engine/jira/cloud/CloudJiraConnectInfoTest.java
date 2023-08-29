//package com.engine.jira.cloud;
//
//import com.arms.model.jiraproject.cloud.jira.CloudJiraProjectDTO;
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
//        CloudJiraConnectInfoDTO found = cloudJiraConnectInfo.checkInfo("1");
//        WebClient webClient = 지라유틸.클라우드_통신기_생성(found.getUri(), found.getEmail(), found.getToken());
//
//        CloudJiraProjectDTO cloudJiraProjectDTO
//                        = 지라유틸.get(webClient,
//                                        "/rest/api/3/project/ADVANC2D",
//                                        CloudJiraProjectDTO.class).block();
//
//        assertThat(cloudJiraProjectDTO.getId()).isEqualTo("10000");
//    }
//}
