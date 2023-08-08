package com.engine.jira.cloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueDTO;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueSearchDTO;
import com.arms.jira.cloud.jiraissue.model.FieldsDTO.IssueLink;

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
        String uri = "/rest/api/3/search?jql=project=" + projectKeyOrId;

        CloudJiraIssueSearchDTO issues = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(CloudJiraIssueSearchDTO.class).block();

        Assertions.assertThat(issues.getIssues().getClass()).isEqualTo(ArrayList.class);
    }

    @Test
    @DisplayName("이슈 상세조회 조회 테스트")
    public void IssueDetailCallTest() {
        CloudJiraIssueDTO issue = getIssue(issueKeyOrId);
        Assertions.assertThat(issue.getSelf()).isEqualTo("https://advanc2d.atlassian.net/rest/api/3/issue/10010");
    }

    @Test
    public void test() {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            IssueDTO rootIssue = fetchLinkedIssues(baseUrl, "ADVANC2D-35", httpClient, id, pass);
            IssueDTO outIssue = fetchOutLinkedIssues(baseUrl, "ADVANC2D-35", httpClient, id, pass);
            printLinkedIssues(rootIssue, 0);
            printLinkedIssues(outIssue, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private IssueDTO fetchLinkedIssues(String jiraBaseUrl, String issueKey, HttpClient httpClient, String username, String password) throws IOException, JSONException {
        String issueUrl = jiraBaseUrl + "/rest/api/3/issue/" + issueKey;
        HttpGet issueRequest = new HttpGet(issueUrl);
        issueRequest.addHeader("Authorization", "Basic " + getBase64Credentials(username, password));
        HttpResponse issueResponse = httpClient.execute(issueRequest);
        String issueResponseJson = EntityUtils.toString(issueResponse.getEntity());
        JSONObject issueJson = new JSONObject(issueResponseJson);

        IssueDTO issueDTO = new IssueDTO(issueJson.getString("key"));

        JSONArray issueLinks = issueJson.getJSONObject("fields").getJSONArray("issuelinks");
        for (int i = 0; i < issueLinks.length(); i++) {
            JSONObject link = issueLinks.getJSONObject(i);
//            if (link.has("outwardIssue")) {
//                String linkedIssueKey = link.getJSONObject("outwardIssue").getString("key");
//                IssueDTO linkedIssueDTO = fetchLinkedIssues(jiraBaseUrl, linkedIssueKey, httpClient, username, password);
//                issueDTO.linkedIssues.add(linkedIssueDTO);
//            }

            if (link.has("inwardIssue")) {
                String linkedIssueKey = link.getJSONObject("inwardIssue").getString("key");
                IssueDTO linkedIssueDTO = fetchLinkedIssues(jiraBaseUrl, linkedIssueKey, httpClient, username, password);
                issueDTO.linkedIssues.add(linkedIssueDTO);
            }
        }

        return issueDTO;
    }

    private IssueDTO fetchOutLinkedIssues(String jiraBaseUrl, String issueKey, HttpClient httpClient, String username, String password) throws IOException, JSONException {
        String issueUrl = jiraBaseUrl + "/rest/api/3/issue/" + issueKey;
        CloudJiraIssueDTO cloudJiraIssueDTO = getIssue(issueKey);

        IssueDTO issueDTO = new IssueDTO(cloudJiraIssueDTO.getKey());

        List<IssueLink> issueLinks = cloudJiraIssueDTO.getFields().getIssuelinks();
        for (int i = 0; i < issueLinks.size(); i++) {
            IssueLink link = issueLinks.get(i);
            if (link.getOutwardIssue() != null) {
                String linkedIssueKey = link.getOutwardIssue().getKey();
                IssueDTO linkedIssueDTO = fetchOutLinkedIssues(jiraBaseUrl, linkedIssueKey, httpClient, username, password);
                issueDTO.linkedIssues.add(linkedIssueDTO);
            }

//            if (link.has("inwardIssue")) {
//                String linkedIssueKey = link.getJSONObject("inwardIssue").getString("key");
//                IssueDTO linkedIssueDTO = fetchLinkedIssues(jiraBaseUrl, linkedIssueKey, httpClient, username, password);
//                issueDTO.linkedIssues.add(linkedIssueDTO);
//            }
        }

        return issueDTO;
    }

    private static void printLinkedIssues(IssueDTO issueDTO, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "Issue: " + issueDTO.key);
        for (IssueDTO linkedIssue : issueDTO.linkedIssues) {
            printLinkedIssues(linkedIssue, depth + 1);
        }
    }

    class IssueDTO {
        String key;
        List<IssueDTO> linkedIssues;

        public IssueDTO(String key) {
            this.key = key;
            this.linkedIssues = new ArrayList<>();
        }
    }
}
