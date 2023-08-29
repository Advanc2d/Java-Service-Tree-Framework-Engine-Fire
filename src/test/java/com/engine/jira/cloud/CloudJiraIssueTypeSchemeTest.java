package com.engine.jira.cloud;

import com.arms.jira.cloud.jiraissuetypescheme.model.CloudJiraIssueTypeSchemeMappingDTO;
import com.arms.jira.cloud.jiraissuetypescheme.model.CloudJiraIssueTypeSchemeMappingValueDTO;
import com.arms.jira.cloud.jiraissuetypescheme.model.IssueTypeIdsDTO;
import com.arms.jira.info.service.지라연결_서비스;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.*;

@SpringBootTest
public class CloudJiraIssueTypeSchemeTest {
    WebClient webClient;

    public String baseUrl = "https://advanc2d.atlassian.net";
    public String id = "gkfn185@gmail.com";
    public String pass = "ATATT3xFfGF0OhyPJU1DlcjJmtsZBXsuXPmet-VBfz07AN6R_vGsV6rOeO6loKVV7iEBsMsmW0WPO4vpPokpcRR_QMrpHi9VJtWdLDLKrhG27j6aGFCeQh5_0sDjWjK45jcJsmQ606vB2Mt9ZYfSAdrRRjlUHceqBiU_Mq7--spJIpAOy7Wi0w4=0122341F";
    public String projectKeyOrId = "ADVANC2D";
    public String projectId = "10000";
    public String issueTypeId = "10028";

    @Autowired
    지라연결_서비스 지라연결_서비스;

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
    @DisplayName("매핑된 issueTypeScheme에 따르는 issueTypeId 전체 조회 api 호출 테스트")
    public void EachIssueTypeSchemeMappingIssueTypeIdCallTest() throws Exception {

        int startAt = 0;
        int 최대_검색수 = 50;
        int index=1;
        boolean checkLast = false;

        List<CloudJiraIssueTypeSchemeMappingValueDTO> values
                    = new ArrayList<CloudJiraIssueTypeSchemeMappingValueDTO>();

        while(!checkLast) {
            String uri = "/rest/api/3/issuetypescheme/mapping?최대_검색수="+ 최대_검색수 + "&startAt=" + startAt;
            CloudJiraIssueTypeSchemeMappingDTO issueTypeSchemeMapping = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<CloudJiraIssueTypeSchemeMappingDTO>() {}).block();

            values.addAll(issueTypeSchemeMapping.getValues());

            if (issueTypeSchemeMapping.getTotal() == values.size()) {
                checkLast = true;
            }
            else {
                startAt = 최대_검색수 * index;
                index++;
            }
        }

        Map<String, List<String>> issueTypeMap = getIssueTypeMapping(values);

        String issueTypeId = 지라연결_서비스.getIssueTypeId(1L);

        for (Map.Entry<String, List<String>> entry : issueTypeMap.entrySet()) {
            String issueTypeSchemeId = entry.getKey();
            List<String> issueTypeIds = entry.getValue();

            if (issueTypeIds.contains(issueTypeId)) {
                System.out.println(issueTypeSchemeId + "에는 원하는 issueTypeId(" + issueTypeId + ")가 존재합니다.");
            } else {
                System.out.println(issueTypeSchemeId+ "에는 원하는 issueTypeId(" + issueTypeId + ")가 존재하지 않습니다.");
            }

        }
    }

    @Test
    @DisplayName("해당 이슈 타입 스킴에 이슈 타입 추가 api 테스트")
    public void addIssueTypesToIssueTypeScheme(/*String issueTypeSchemeId, String issueTypeId*/) {
        String issueTypeSchemeId = "10137";
        String uri = "/rest/api/3/issuetypescheme/"+issueTypeSchemeId+"/issuetype";
        List<String> issueTypeIds = new ArrayList<String>();
        issueTypeIds.add(issueTypeId);

        IssueTypeIdsDTO dto = new IssueTypeIdsDTO();
        dto.setIssueTypeIds(issueTypeIds);

        ResponseEntity<?> addIssueTypeScheme = webClient.put()
                .uri(uri)
                .body(Mono.just(dto), IssueTypeIdsDTO.class)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class)
                        .map(body -> clientResponse.statusCode().is2xxSuccessful() ? ResponseEntity.ok(body) : ResponseEntity.status(clientResponse.statusCode()).build())
                        .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()))
                        .onErrorResume(WebClientResponseException.class, ex -> Mono.error(new Exception(ex.getMessage()))))
                .block();

        System.out.println(addIssueTypeScheme.getStatusCode() == HttpStatus.NO_CONTENT);
    }

    // issueTypeSchemeId에 따른 issueTypeId 종류를 매핑하는 메서드
    public static Map<String, List<String>> getIssueTypeMapping(List<CloudJiraIssueTypeSchemeMappingValueDTO> values) {
        Map<String, List<String>> issueTypeMap = new HashMap<>();

        for (CloudJiraIssueTypeSchemeMappingValueDTO item : values) {
            String issueTypeSchemeId = item.getIssueTypeSchemeId();
            String issueTypeId = item.getIssueTypeId();

            if (issueTypeSchemeId != null && issueTypeId != null) {
                if (issueTypeMap.containsKey(issueTypeSchemeId)) {
                    issueTypeMap.get(issueTypeSchemeId).add(issueTypeId);
                } else {
                    List<String> issueTypeIds = new ArrayList<>();
                    issueTypeIds.add(issueTypeId);
                    issueTypeMap.put(issueTypeSchemeId, issueTypeIds);
                }
            }
        }

        return issueTypeMap;
    }
}
