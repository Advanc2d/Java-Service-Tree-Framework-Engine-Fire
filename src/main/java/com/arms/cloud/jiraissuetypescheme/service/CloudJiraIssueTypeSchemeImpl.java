package com.arms.cloud.jiraissuetypescheme.service;

import com.arms.cloud.CloudJiraUtils;
import com.arms.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoDTO;
import com.arms.cloud.jiraconnectinfo.service.CloudJiraConnectInfo;
import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeDTO;
import com.arms.cloud.jiraissuetype.service.CloudJiraIssueType;
import com.arms.cloud.jiraissuetypescheme.domain.CloudJiraIssueTypeSchemeMappingDTO;
import com.arms.cloud.jiraissuetypescheme.domain.CloudJiraIssueTypeSchemeMappingValueDTO;
import com.arms.cloud.jiraissuetypescheme.domain.IssueTypeIdsDTO;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service("cloudJiraIssueTypeScheme")
public class CloudJiraIssueTypeSchemeImpl implements CloudJiraIssueTypeScheme {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CloudJiraIssueType cloudJiraIssueType;

    @Autowired
    private CloudJiraConnectInfo cloudJiraConnectInfo;

    public CloudJiraIssueTypeSchemeMappingDTO getIssueTypeSchemeMapping(String connectId) {

        int maxResult = 50;
        int startAt = 0;
        int index=1;
        boolean checkLast = false;

        List<CloudJiraIssueTypeSchemeMappingValueDTO> values
                = new ArrayList<CloudJiraIssueTypeSchemeMappingValueDTO>();

        CloudJiraConnectInfoDTO found = cloudJiraConnectInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getEmail(), found.getToken());

        CloudJiraIssueTypeSchemeMappingDTO issueTypeSchemeMapping = null;

        while(!checkLast) {

            String endpoint = "/rest/api/3/issuetypescheme/mapping?maxResults="+ maxResult + "&startAt=" + startAt;
            CloudJiraIssueTypeSchemeMappingDTO issueTypeSchemeMappingPaging 
                    = CloudJiraUtils.get(webClient, endpoint, CloudJiraIssueTypeSchemeMappingDTO.class).block();

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

    public Map<String,Object> addIssueTypeSchemeReqIssueType(String connectId) throws Exception {

        CloudJiraIssueTypeSchemeMappingDTO issueTypeSchemeMapping = getIssueTypeSchemeMapping(connectId);
        List<CloudJiraIssueTypeSchemeMappingValueDTO> values = issueTypeSchemeMapping.getValues();
        Map<String, List<String>> issueTypeMap = getIssueTypeMapping(values);

        List<CloudJiraIssueTypeDTO> list =  cloudJiraIssueType.getIssueTypeListByDB();

        Map<String,Object> result = new HashMap<String,Object>();

        for (Map.Entry<String, List<String>> entry : issueTypeMap.entrySet()) {
            String issueTypeSchemeId = entry.getKey();
            List<String> issueTypeIds = entry.getValue();

            for(CloudJiraIssueTypeDTO item : list) {
                if (issueTypeIds.contains(item.getId())) {
                    System.out.println(issueTypeSchemeId + "에는 원하는 issueTypeId(" + item.getId() + ")가 존재합니다.");
                } else {
                    System.out.println(issueTypeSchemeId+ "에는 원하는 issueTypeId(" + item.getId() + ")가 존재하지 않습니다.");
                    Boolean response = addIssueTypesToIssueTypeScheme(connectId, issueTypeSchemeId, item.getId());

                    if (response == false) {
                        result.put("success", response);
                        result.put("message", "Issue Type Scheme에 추가하는 중 오류가 발생하였습니다.");
                        return result;
                    }
                }
            }
        }

        if (result.size() == 0) {
            result.put("success", true);
            result.put("message", "Issue Type Scheme 스케줄러 완료");
        }

        return result;
    }

    public boolean addIssueTypesToIssueTypeScheme(String connectId, String issueTypeSchemeId, String issueTypeId) {

        String endpoint = "/rest/api/3/issuetypescheme/"+issueTypeSchemeId+"/issuetype";
        
        List<String> issueTypeIds = new ArrayList<String>();
        issueTypeIds.add(issueTypeId);

        IssueTypeIdsDTO dto = new IssueTypeIdsDTO();
        dto.setIssueTypeIds(issueTypeIds);

        CloudJiraConnectInfoDTO found = cloudJiraConnectInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getEmail(), found.getToken());

        Optional<Boolean> result = CloudJiraUtils.executePut(webClient, dto, endpoint);

        // Mono<Void> addIssueTypeScheme = CloudJiraUtils.put(webClient, endpoint, dto, Void.class);
        // try {
        //     addIssueTypeScheme
        //         .onErrorResume(e -> Mono.empty())
        //         .block();
        //     isSuccess = true;
        // } catch (Exception e) {
        //     // 에러 핸들링 (해당 예외 발생 시에는 isSuccess가 false로 유지됩니다)
        // }

        boolean isSuccess = false;

        if (result.isPresent()) {
            if (result.get()) {
                // PUT 호출이 HTTP 204로 성공했습니다.
                isSuccess = true;
            } 
        }

        // ResponseEntity<?> addIssueTypeScheme = jiraWebClient.put()
        //         .uri(uri)
        //         .body(Mono.just(dto), IssueTypeIdsDTO.class)
        //         .exchange()
        //         .flatMap(clientResponse -> clientResponse.bodyToMono(String.class)
        //                 .map(body -> clientResponse.statusCode().is2xxSuccessful() ? ResponseEntity.ok(body) : ResponseEntity.status(clientResponse.statusCode()).build())
        //                 .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()))
        //                 .onErrorResume(WebClientResponseException.class, ex -> Mono.error(new Exception(ex.getMessage()))))
        //         .block();

        return isSuccess;
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
