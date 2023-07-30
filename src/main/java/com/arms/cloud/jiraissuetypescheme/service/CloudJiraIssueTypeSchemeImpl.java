package com.arms.cloud.jiraissuetypescheme.service;

import com.arms.cloud.jiraissuetypescheme.domain.CloudJiraIssueTypeSchemeMappingDTO;
import com.arms.cloud.jiraissuetypescheme.domain.CloudJiraIssueTypeSchemeMappingValueDTO;
import com.arms.cloud.jiraissuetypescheme.domain.IssueTypeIdsDTO;
import com.arms.config.CloudJiraConfig;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service("cloudJiraIssueTypeScheme")
public class CloudJiraIssueTypeSchemeImpl implements CloudJiraIssueTypeScheme {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("cloudJiraConfig")
    private CloudJiraConfig cloudJiraConfig;

    public CloudJiraIssueTypeSchemeMappingDTO getIssueTypeSchemeMapping() {
        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

        int maxResult = 50;
        int startAt = 0;
        int index=1;
        boolean checkLast = false;

        List<CloudJiraIssueTypeSchemeMappingValueDTO> values
                = new ArrayList<CloudJiraIssueTypeSchemeMappingValueDTO>();

        CloudJiraIssueTypeSchemeMappingDTO issueTypeSchemeMapping = null;

        while(!checkLast) {
            String uri = "/rest/api/3/issuetypescheme/mapping?maxResults="+ maxResult + "&startAt=" + startAt;
            CloudJiraIssueTypeSchemeMappingDTO issueTypeSchemeMappingPaging = jiraWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<CloudJiraIssueTypeSchemeMappingDTO>() {}).block();

            values.addAll(issueTypeSchemeMappingPaging.getValues());

            if (issueTypeSchemeMappingPaging.getTotal() == values.size()) {
                issueTypeSchemeMapping = issueTypeSchemeMappingPaging;
                checkLast = true;
            }
            else {
                startAt = maxResult * index;
                index++;
            }
        }

        issueTypeSchemeMapping.setValues(values);

        return issueTypeSchemeMapping;
    }

    public String addIssueTypeSchemeReqIssueType() {
        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

        CloudJiraIssueTypeSchemeMappingDTO issueTypeSchemeMapping = getIssueTypeSchemeMapping();

        List<CloudJiraIssueTypeSchemeMappingValueDTO> values = issueTypeSchemeMapping.getValues();
        Map<String, List<String>> issueTypeMap = getIssueTypeMapping(values);

        // 수정필요
        // DB에서 요구사항 issueType 정보 가져오기
        String issueTypeId = "10028";

        for (Map.Entry<String, List<String>> entry : issueTypeMap.entrySet()) {
            String issueTypeSchemeId = entry.getKey();
            List<String> issueTypeIds = entry.getValue();

            if (issueTypeIds.contains(issueTypeId)) {
                System.out.println(issueTypeSchemeId + "에는 원하는 issueTypeId(" + issueTypeId + ")가 존재합니다.");
            } else {
                System.out.println(issueTypeSchemeId+ "에는 원하는 issueTypeId(" + issueTypeId + ")가 존재하지 않습니다.");
                ResponseEntity<?>  result = addIssueTypesToIssueTypeScheme(issueTypeSchemeId, issueTypeId);

                if (result.getStatusCode() == HttpStatus.NO_CONTENT) {
                    System.out.println("이슈 타입 추가 성공");
                }
                else {
                    System.out.println("이슈 타입 추가 실패");
                    return result.toString();
                }
            }
        }
        return "success";
    }

    public ResponseEntity<?> addIssueTypesToIssueTypeScheme(String issueTypeSchemeId, String issueTypeId) {
        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();
        String uri = "/rest/api/3/issuetypescheme/"+issueTypeSchemeId+"/issuetype";
        List<String> issueTypeIds = new ArrayList<String>();
        issueTypeIds.add(issueTypeId);

        IssueTypeIdsDTO dto = new IssueTypeIdsDTO();
        dto.setIssueTypeIds(issueTypeIds);

        ResponseEntity<?> addIssueTypeScheme = jiraWebClient.put()
                .uri(uri)
                .body(Mono.just(dto), IssueTypeIdsDTO.class)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class)
                        .map(body -> clientResponse.statusCode().is2xxSuccessful() ? ResponseEntity.ok(body) : ResponseEntity.status(clientResponse.statusCode()).build())
                        .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()))
                        .onErrorResume(WebClientResponseException.class, ex -> Mono.error(new Exception(ex.getMessage()))))
                .block();

        return addIssueTypeScheme;
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
