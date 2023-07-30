package com.arms.cloud;

import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeDTO;
import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeInputDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.List;

public class CloudJiraIssueTypeTest {
    WebClient webClient;

    public String baseUrl = "https://advanc2d.atlassian.net";
    public String id = "gkfn185@gmail.com";
    public String pass = "ATATT3xFfGF0OhyPJU1DlcjJmtsZBXsuXPmet-VBfz07AN6R_vGsV6rOeO6loKVV7iEBsMsmW0WPO4vpPokpcRR_QMrpHi9VJtWdLDLKrhG27j6aGFCeQh5_0sDjWjK45jcJsmQ606vB2Mt9ZYfSAdrRRjlUHceqBiU_Mq7--spJIpAOy7Wi0w4=0122341F";
    public String projectKeyOrId = "ADVANC2D";
    public String projectId = "10000";

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
    @DisplayName("이슈 타입 전체 조회 테스트")
    public void IssueTypeCallTest() {
        String uri = "/rest/api/3/issuetype";

        List<CloudJiraIssueTypeDTO> issuetypes = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(List.class).block();

        Assertions.assertThat(issuetypes.size()).isEqualTo(19);
    }

    @Test
    @DisplayName("이슈 타입 추가 테스트")
    public void IssueTypeCreateTest() {
        String uri = "/rest/api/3/issuetype";
        CloudJiraIssueTypeInputDTO addIssueTypeDTO
                = new CloudJiraIssueTypeInputDTO("하위 요구사항 설명2" , "하위 요구사항2", -1);
        CloudJiraIssueTypeDTO issuetypes = webClient.post()
                .uri(uri)
                .body(BodyInserters.fromValue(addIssueTypeDTO))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CloudJiraIssueTypeDTO>() {}).block();

        Assertions.assertThat(issuetypes.getName()).isEqualTo("하위 요구사항2");
    }

    @Test
    @DisplayName("각 프로젝트 별 이슈 타입 전체 조회 테스트")
    public void EachProjectIssueTypeCallTest() {
        String uri = "/rest/api/3/issuetype/project?projectId=" + "10001";

        List<CloudJiraIssueTypeDTO> issuetypes = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CloudJiraIssueTypeDTO>>() {}).block();

        boolean check = false;
        for (CloudJiraIssueTypeDTO issuetype : issuetypes) {
            if ("요구사항".equals(issuetype.getName())) {
                check = true;
                System.out.println("issuetype.getName() = " + issuetype.getName());
            }
        }

        Assertions.assertThat(check).isEqualTo(true);
    }
}
