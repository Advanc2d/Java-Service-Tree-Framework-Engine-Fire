package com.engine.jira.onpremise;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class OnPremiseJiraIssueTest {
    JiraRestClient restClient;

    public String baseUrl = "http://www.313.co.kr/jira";
    public String id = "admin";
    public String pass = "flexjava";
    public String projectKeyOrId = "ARMS";
    public String issueKeyOrId = projectKeyOrId + "-1";

    @BeforeEach
    void setUp () throws URISyntaxException, IOException {
        restClient = getJiraRestClient(baseUrl, id, pass);
    }

    public static JiraRestClient getJiraRestClient(String jiraUrl, String jiraID, String jiraPass) throws URISyntaxException, IOException {
        final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        return factory.createWithBasicHttpAuthentication(new URI(jiraUrl), jiraID, jiraPass);
    }

    @Test
    @DisplayName("이슈 타입이 요구사항인 이슈를 전체 조회하고 이슈 링크 내용을 전부 가져오는 스케줄러")
    public void test() throws Exception {
        SearchResult searchResult = getIssueListByIssueTypeName("Requirement");

        List<Issue> issues = (List<Issue>) searchResult.getIssues();

        try {
            for (Issue issue : issues) {
                String id = issue.getId().toString();
                IssueDTO linkedIssue = fetchLinkedIssues(id);
                printLinkedIssues(linkedIssue, 0);
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

    public Issue getIssue(String issueKeyOrId) throws Exception {

        Issue issue = restClient.getIssueClient().getIssue(issueKeyOrId).claim();

        return issue;
    }

    public SearchResult getIssueListByIssueTypeName(String issueTypeName) throws Exception {

        String jql = "issuetype = " + issueTypeName;
        int maxResult = 50;
        int startAt = 0;
        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        // 이슈 건수가 1000이 넘을때 이슈 조회를 위한 처리
        List<Issue> allIssues = new ArrayList<>();
        SearchResult searchResult;

        do {
            searchResult = restClient.getSearchClient()
                    .searchJql(jql, maxResult, startAt, fields)
                    .get();
            for (Issue issue : searchResult.getIssues()) {
                allIssues.add(issue);
            }
            startAt += maxResult;
        } while (searchResult.getTotal() > startAt);

        // 변환을 위한 ObjectMapper 생성
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JodaModule()); //Date 처리 위함
//        // 이슈 리스트를 json 형식으로 변환
//        JsonNode issuesAsJson = null;
//        try {
//            Map<String, Object> resultData = new HashMap<>();
//            resultData.put("total", allIssues.size());
//            resultData.put("issues", allIssues);
//
//            issuesAsJson = objectMapper.valueToTree(resultData);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return searchResult;
    }

    private IssueDTO fetchLinkedIssues(String issueKeyOrId) throws Exception {

        Issue issue = getIssue(issueKeyOrId);

        IssueDTO issueDTO = new IssueDTO(issue.getId().toString(), issue.getKey(), issue.getSelf().toString());
        List<IssueLink> issueLinks = (List<IssueLink>) issue.getIssueLinks();

        for (IssueLink issueLink : issueLinks) {

//            if (link.has("outwardIssue")) {
//                String linkedIssueKey = link.getJSONObject("outwardIssue").getString("key");
//                IssueDTO linkedIssueDTO = fetchLinkedIssues(jiraBaseUrl, linkedIssueKey, httpClient, username, password);
//                issueDTO.linkedIssues.add(linkedIssueDTO);
//            }

            System.out.println(issue.getKey() + "의 이슈 링크 타입" +issueLink.getIssueLinkType().getName()+ " ㄱㄱㄱㄱㄱ = " + issueLink.getIssueLinkType().getDescription());
            if (issueLink.getTargetIssueKey() != null || !issueLink.getTargetIssueKey().isEmpty()) {
                String linkedIssueKey = issueLink.getTargetIssueKey();
                IssueDTO linkedIssueDTO = fetchLinkedIssues(linkedIssueKey);
                issueDTO.linkedIssues.add(linkedIssueDTO);
            }
        }

        return issueDTO;
    }

//    private CloudJiraIssueTest.IssueDTO fetchOutLinkedIssues(String jiraBaseUrl, String issueKey) throws IOException, JSONException {
//        String issueUrl = jiraBaseUrl + "/rest/api/3/issue/" + issueKey;
//        CloudJiraIssueDTO cloudJiraIssueDTO = getIssue(issueKey);
//
//        IssueDTO issueDTO = new IssueDTO(cloudJiraIssueDTO.getKey());
//
//        List<FieldsDTO.IssueLink> issueLinks = cloudJiraIssueDTO.getFields().getIssuelinks();
//        for (int i = 0; i < issueLinks.size(); i++) {
//            FieldsDTO.IssueLink link = issueLinks.get(i);
//            if (link.getOutwardIssue() != null) {
//                String linkedIssueKey = link.getOutwardIssue().getKey();
//                IssueDTO linkedIssueDTO = fetchOutLinkedIssues(jiraBaseUrl, linkedIssueKey);
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

    private static void printLinkedIssues(IssueDTO issueDTO, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "Issue: " + issueDTO.key);
        for (IssueDTO linkedIssue : issueDTO.linkedIssues) {
            printLinkedIssues(linkedIssue, depth + 1);
        }
    }

    class IssueDTO {
        String id;
        String key;
        String self;
        List<IssueDTO> linkedIssues;

        public IssueDTO(String id, String key, String self) {
            this.key = key;
            this.id = id;
            this.self = self;
            this.linkedIssues = new ArrayList<>();
        }
    }
}
