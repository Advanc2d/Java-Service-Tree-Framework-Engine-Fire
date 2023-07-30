package com.arms.cloud;

import java.util.Base64;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.cloud.jiraissue.domain.CloudJiraIssueDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueSearchDTO;
import com.arms.cloud.jiraissue.service.CloudJiraIssue;

public class CloudJiraIssueTest {
    WebClient webClient;

    public String baseUrl = "https://advanc2d.atlassian.net";
    public String id = "gkfn185@gmail.com";
    public String pass = "ATATT3xFfGF0OhyPJU1DlcjJmtsZBXsuXPmet-VBfz07AN6R_vGsV6rOeO6loKVV7iEBsMsmW0WPO4vpPokpcRR_QMrpHi9VJtWdLDLKrhG27j6aGFCeQh5_0sDjWjK45jcJsmQ606vB2Mt9ZYfSAdrRRjlUHceqBiU_Mq7--spJIpAOy7Wi0w4=0122341F";
    public String projectKeyOrId = "ADVANC2D";
    public String issueKeyOrId = projectKeyOrId + "-7";

    @BeforeEach
    void setUp () {
        webClient = WebClient.builder()
                            .baseUrl(baseUrl)
                            .defaultHeader("Authorization", "Basic " + getBase64Credentials(id, pass))
                            .build();
    }

    private String getBase64Credentials(String jiraID, String jiraPass) {
        String credentials = jiraID + ":" + jiraPass;
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }

    @Test
    @DisplayName("프로젝트 키의 이슈 전체 조회 테스트")
    public void IssueSearchCallTest() {
        String uri = "/rest/api/3/search?jql=project=" + projectKeyOrId;

        CloudJiraIssueSearchDTO issues = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(CloudJiraIssueSearchDTO.class).block();

        Assertions.assertThat(issues.getIssues().size()).isEqualTo(6);
    }
    
    @Test
    @DisplayName("이슈 상세조회 조회 테스트")
    public void IssueDetailCallTest() {
        String uri = "/rest/api/3/issue/" + issueKeyOrId;

        CloudJiraIssueDTO issue = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(CloudJiraIssueDTO.class).block();

        Assertions.assertThat(issue.getSelf()).isEqualTo("https://advanc2d.atlassian.net/rest/api/3/issue/10019");
    }

    
}
