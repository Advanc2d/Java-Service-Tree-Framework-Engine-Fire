package com.engine.jira.onpremise.scheduler;

import com.arms.jira.utils.지라유틸;
import com.arms.jira.onpremise.jiraissue.model.FieldsDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueEntity;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class OnPremiseIssueSchedulerTest {
    JiraRestClient restClient;
    public static ModelMapper modelMapper = new ModelMapper();
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
        List<OnPremiseJiraIssueDTO> issues = new ArrayList<>();
        Iterable<Issue> issues1 = searchResult.getIssues();
        for (Issue issue1 : issues1) {
            if(issue1.getProject().getKey().equals("PHM")) {
                OnPremiseJiraIssueDTO onPremiseJiraIssueDTO = new OnPremiseJiraIssueDTO();
                onPremiseJiraIssueDTO.setId(issue1.getId().toString());
                onPremiseJiraIssueDTO.setKey(issue1.getKey());
                onPremiseJiraIssueDTO.setSelf(issue1.getSelf().toString());

                issues.add(onPremiseJiraIssueDTO);
            }
        }

        List<OnPremiseJiraIssueEntity> allDtos = new ArrayList<>();
        try {
            for (OnPremiseJiraIssueDTO issue : issues) {
                List<OnPremiseJiraIssueEntity> issueLinkDTOs = findAllLinkedDtos(issue, new ArrayList<>(), null, null);

                allDtos.addAll(issueLinkDTOs);
                // printLinkedIssues(issueLinkDTO, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("allDtos = " + allDtos.size());
        for (OnPremiseJiraIssueEntity dto : allDtos) {
            System.out.println(dto.getKey() +"/"+ dto.getOutwardId() +"/"+ dto.getParentId());
        }
    }

    public List<OnPremiseJiraIssueEntity> findAllLinkedDtos(OnPremiseJiraIssueDTO dto, List<OnPremiseJiraIssueEntity> allDtos,
                                                            String outwardId, String parentId) throws Exception {
        // 현재 DTO의 하위에 연결된 DTO들을 allDtos에 추가
        OnPremiseJiraIssueEntity entity = modelMapper.map(dto, OnPremiseJiraIssueEntity.class);

        if (outwardId != null) {
            entity.setOutwardId(outwardId);
        }

        if(parentId != null) {
            entity.setParentId(parentId);
        }

        allDtos.add(entity);

        System.out.println("dto.getKey() = " + dto.getKey());
        dto = getIssueByWebClient(dto.getKey());

        // 현재 DTO와 연결된 모든 하위 DTO를 탐색
        if (dto.getFields().getIssuelinks() != null) {
            for (FieldsDTO.IssueLink issueLink : dto.getFields().getIssuelinks()) {
                if (issueLink.getInwardIssue() == null) {
                    continue;
                }

                System.out.println("issueLink.getInwardIssue().getKey() = " + issueLink.getInwardIssue().getKey());
                OnPremiseJiraIssueDTO onPremiseJiraIssueDTO = getIssueByWebClient(issueLink.getInwardIssue().getKey());

                findAllLinkedDtos(onPremiseJiraIssueDTO, allDtos, dto.getId(), null);
            }
        }

        if (dto.getFields().getSubtasks() != null ) {
            for (OnPremiseJiraIssueDTO subtask : dto.getFields().getSubtasks()) {
                System.out.println("subtask.getKey() = " + subtask.getKey());
                OnPremiseJiraIssueDTO subtaskDTO = getIssueByWebClient(subtask.getKey());
                findAllLinkedDtos(subtaskDTO, allDtos, null, dto.getId());
            }
        }

        return allDtos;
    }

    public Issue getIssue(String issueKeyOrId) throws Exception {

        Issue issue = restClient.getIssueClient().getIssue(issueKeyOrId).claim();

        return issue;
    }

    public SearchResult getIssueListByIssueTypeName(String issueTypeName) throws Exception {

        String jql = "issuetype = " + issueTypeName;

        int startAt = 0;
        int 최대_검색수 = 50;
        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        // 이슈 건수가 1000이 넘을때 이슈 조회를 위한 처리
        List<Issue> allIssues = new ArrayList<>();
        SearchResult searchResult;

        do {
            searchResult = restClient.getSearchClient()
                    .searchJql(jql, 최대_검색수, startAt, fields)
                    .get();
            for (Issue issue : searchResult.getIssues()) {
                allIssues.add(issue);
            }
            startAt += 최대_검색수;
        } while (searchResult.getTotal() > startAt);

        return searchResult;
    }

    public OnPremiseJiraIssueDTO getIssueByWebClient(String issueKeyOrId) throws Exception {

        String endpoint = "/rest/api/2/issue/" + issueKeyOrId;
        WebClient webClient = 클라우드_통신기_생성(baseUrl);

        OnPremiseJiraIssueDTO onPremiseJiraIssueDTO = 지라유틸.get(webClient, endpoint, OnPremiseJiraIssueDTO.class).block();

        return onPremiseJiraIssueDTO;
    }

    public static WebClient 클라우드_통신기_생성(String uri) {

        return WebClient.builder()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
