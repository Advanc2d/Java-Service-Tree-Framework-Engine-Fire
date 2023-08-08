package com.engine.jira.cloud;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.arms.jira.cloud.jiraproject.model.CloudJiraProjectDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class CloudJiraProjectTest {
    WebClient webClient;

    public String baseUrl = "https://advanc2d.atlassian.net/";
    public String id = "gkfn185@gmail.com";
    public String pass = "ATATT3xFfGF0OhyPJU1DlcjJmtsZBXsuXPmet-VBfz07AN6R_vGsV6rOeO6loKVV7iEBsMsmW0WPO4vpPokpcRR_QMrpHi9VJtWdLDLKrhG27j6aGFCeQh5_0sDjWjK45jcJsmQ606vB2Mt9ZYfSAdrRRjlUHceqBiU_Mq7--spJIpAOy7Wi0w4=0122341F";
    public String projectKeyOrId = "ADVANC2DF";

    @BeforeEach
    void setUp () {
        webClient = WebClient.builder()
                            .baseUrl(baseUrl)
                            .defaultHeader("Authorization", "Basic " + getBase64Credentials(id, pass))
                            .filter(errorHandlingFilter())
                            .build();
    }


    ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (!response.statusCode().is2xxSuccessful()) {
                response.bodyToMono(String.class)
                        .doOnNext(errorMessage -> System.out.println("에러 메시지: " + errorMessage))
                        .subscribe();
            }
            return Mono.just(response);
        });
    }

    private String getBase64Credentials(String jiraID, String jiraPass) {
        String credentials = jiraID + ":" + jiraPass;
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }

    @Test
    @DisplayName("프로젝트 전체 조회 테스트")
    public void ProjectListCallTest() {
        String uri = "/rest/api/3/project";

        List<CloudJiraProjectDTO> projects = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(List.class).block();

        System.out.println(projects.toString());
        System.out.println(projects.getClass());

        Assertions.assertThat(projects.getClass()).isEqualTo(ArrayList.class);
    }

    @Test
    @DisplayName("프로젝트 세부 사항 조회 테스트")
    public void ProjectDetailsCallTest() {
        String uri = "/rest/api/3/project/" + projectKeyOrId;

        CloudJiraProjectDTO project = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(CloudJiraProjectDTO.class).block();

        Assertions.assertThat(project.getId()).isEqualTo("10000");
    }
}
