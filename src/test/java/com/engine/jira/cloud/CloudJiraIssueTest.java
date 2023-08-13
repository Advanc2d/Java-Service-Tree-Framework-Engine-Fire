package com.engine.jira.cloud;

import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueDTO;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueInputDTO;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueSearchDTO;
import com.arms.jira.cloud.jiraissue.model.FieldsDTO;
import com.arms.jira.cloud.jiraissue.model.FieldsDTO.IssueLink;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;

public class CloudJiraIssueTest {
    WebClient webClient;

    public String baseUrl = "https://advanc2d.atlassian.net";
    public String id = "gkfn185@gmail.com";
    public String pass = "ATATT3xFfGF0OhyPJU1DlcjJmtsZBXsuXPmet-VBfz07AN6R_vGsV6rOeO6loKVV7iEBsMsmW0WPO4vpPokpcRR_QMrpHi9VJtWdLDLKrhG27j6aGFCeQh5_0sDjWjK45jcJsmQ606vB2Mt9ZYfSAdrRRjlUHceqBiU_Mq7--spJIpAOy7Wi0w4=0122341F";
    public String projectKeyOrId = "ADVANC2D";
    public String issueKeyOrId = projectKeyOrId + "-1";

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

    public CloudJiraIssueDTO getIssue(String issueIdOrKey) {
        String uri = "/rest/api/3/issue/" + issueIdOrKey;

        CloudJiraIssueDTO issue = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CloudJiraIssueDTO.class).block();

        return issue;
    }

    @Test
    @DisplayName("프로젝트 키의 이슈 전체 조회 테스트")
    public void IssueSearchCallTest() {
        CloudJiraIssueSearchDTO issues = getIssueByProjectKeyOrId(projectKeyOrId);

        Assertions.assertThat(issues.getIssues().getClass()).isEqualTo(ArrayList.class);
    }

    public CloudJiraIssueSearchDTO getIssueByProjectKeyOrId (String projectKeyOrId) {
        String uri = "/rest/api/3/search?jql=project=" + projectKeyOrId;

        CloudJiraIssueSearchDTO issues = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CloudJiraIssueSearchDTO.class).block();

        return issues;
    }

    public CloudJiraIssueSearchDTO getIssueListByIssueTypeName(String issueTypName) {
        String uri = "/rest/api/3/search?jql=issuetype=" + issueTypName;

        CloudJiraIssueSearchDTO issues = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CloudJiraIssueSearchDTO.class).block();

        return issues;
    }

    @Test
    @DisplayName("이슈 상세조회 조회 테스트")
    public void IssueDetailCallTest() {
        CloudJiraIssueDTO issue = getIssue(issueKeyOrId);
        Assertions.assertThat(issue.getSelf()).isEqualTo("https://advanc2d.atlassian.net/rest/api/3/issue/10010");
    }

    @Test
    @DisplayName("이슈 타입이 요구사항인 이슈를 전체 조회하고 이슈 링크 내용을 전부 가져오는 스케줄러")
    public void test() {
        CloudJiraIssueSearchDTO issues = getIssueListByIssueTypeName("요구사항");

        try {

            for (CloudJiraIssueDTO issue : issues.getIssues()) {
                CloudJiraIssueDTO issueLinkDTO = fetchLinkedIssues(issue.getId());
                printLinkedIssues(issueLinkDTO, 0);
            }

            // IssueDTO rootIssue = fetchLinkedIssues(baseUrl, "ADVANC2D-1");
            // IssueDTO outIssue = fetchOutLinkedIssues(baseUrl, "ADVANC2D-35", httpClient, id, pass);
            // printLinkedIssues(rootIssue, 0);
            // printLinkedIssues(outIssue, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private CloudJiraIssueDTO fetchLinkedIssues(String issueKeyOrId) throws IOException, JSONException {

        CloudJiraIssueDTO cloudJiraIssueDTO = getIssue(issueKeyOrId);

        CloudJiraIssueDTO childLinkDTO = new CloudJiraIssueDTO(cloudJiraIssueDTO.getId(),
                                                cloudJiraIssueDTO.getKey(), cloudJiraIssueDTO.getSelf());
        List<IssueLink> issueLinks = cloudJiraIssueDTO.getFields().getIssuelinks();

        for (int i = 0; i < issueLinks.size(); i++) {
            IssueLink link = issueLinks.get(i);
//            if (link.has("outwardIssue")) {
//                String linkedIssueKey = link.getJSONObject("outwardIssue").getString("key");
//                IssueDTO linkedIssueDTO = fetchLinkedIssues(jiraBaseUrl, linkedIssueKey, httpClient, username, password);
//                issueDTO.linkedIssues.add(linkedIssueDTO);
//            }

            if (link.getInwardIssue() != null) {
                String linkedIssueKey = link.getInwardIssue().getKey();
                CloudJiraIssueDTO linkedIssueDTO = fetchLinkedIssues(linkedIssueKey);

                if (linkedIssueDTO != null) {
                    childLinkDTO.getIssues().add(linkedIssueDTO);
                }
            }
        }

        return childLinkDTO;
    }

//    private IssueDTO fetchOutLinkedIssues(String jiraBaseUrl, String issueKey, HttpClient httpClient, String username, String password) throws IOException, JSONException {
//        String issueUrl = jiraBaseUrl + "/rest/api/3/issue/" + issueKey;
//        CloudJiraIssueDTO cloudJiraIssueDTO = getIssue(issueKey);
//
//        IssueDTO issueDTO = new IssueDTO(cloudJiraIssueDTO.getKey());
//
//        List<IssueLink> issueLinks = cloudJiraIssueDTO.getFields().getIssuelinks();
//        for (int i = 0; i < issueLinks.size(); i++) {
//            IssueLink link = issueLinks.get(i);
//            if (link.getOutwardIssue() != null) {
//                String linkedIssueKey = link.getOutwardIssue().getKey();
//                IssueDTO linkedIssueDTO = fetchOutLinkedIssues(jiraBaseUrl, linkedIssueKey, httpClient, username, password);
//                issueDTO.linkedIssues.add(linkedIssueDTO);
//            }
//
////            if (link.has("inwardIssue")) {
////                String linkedIssueKey = link.getJSONObject("inwardIssue").getString("key");
////                IssueDTO linkedIssueDTO = fetchLinkedIssues(jiraBaseUrl, linkedIssueKey, httpClient, username, password);
////                issueDTO.linkedIssues.add(linkedIssueDTO);
////            }
//        }
//
//        return issueDTO;
//    }

    private static void printLinkedIssues(CloudJiraIssueDTO issueDTO, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "Issue: " + issueDTO.toString());

        /***
        * DB에 저장 로직 구성
        *** */

        for (CloudJiraIssueDTO linkedIssue : issueDTO.getIssues()) {
            printLinkedIssues(linkedIssue, depth + 1);
        }
    }

    class IssueDTO {
        String id;
        String key;
        String self;
        List<IssueDTO> linkedIssues;

        public IssueDTO(String key) {
            this.key = key;
            this.linkedIssues = new ArrayList<>();
        }
    }

    @Test
    @DisplayName("이슈 수정으로 라벨 처리 테스트")
    public void updatetIssue() {
        Map<String, Object> result = updateIssue(issueKeyOrId);

        Assertions.assertThat(result.get("success")).isEqualTo(true);
    }

    public Map<String, Object> updateIssue(String issueKeyOrId) {
        String uri = "/rest/api/3/issue/" + issueKeyOrId;

        String closedLabel = "closeLabel";

        FieldsDTO fieldsDTO = new FieldsDTO();
        fieldsDTO.setLabels(List.of(closedLabel));

        CloudJiraIssueInputDTO cloudJiraIssueInputDTO = new CloudJiraIssueInputDTO();
        cloudJiraIssueInputDTO.setFields(fieldsDTO);

        Mono<ResponseEntity<Void>> response = webClient.put()
                .uri(uri)
                .body(BodyInserters.fromValue(cloudJiraIssueInputDTO))
                .retrieve()
                .toEntity(Void.class);

        Optional<Boolean> res = response.map(entity -> entity.getStatusCode() == HttpStatus.NO_CONTENT) // 결과가 204인가 확인
                .blockOptional();

        Map<String, Object> result = new HashMap<>();

        boolean isSuccess = false;
        if (res.isPresent()) {
            if (res.get()) {
                // PUT 호출이 HTTP 204로 성공했습니다.
                isSuccess = true;
                result.put("success", isSuccess);
                result.put("message", "이슈 수정 성공하였습니다.");
            }
        }

        if(result ==null || result.size() == 0) {
            result.put("success", isSuccess);
            result.put("message", "이슈 수정 실패하였습니다.");
        }

        return result;
    }
}
