package com.engine.jira.onpremise;

import com.arms.jira.jiraissue.model.지라_이슈_데이터_전송_객체;
import com.arms.jira.jiraissue.model.지라_이슈_필드_데이터_전송_객체;
import com.arms.jira.jiraissueresolution.model.지라_이슈_해결책_데이터_전송_객체;
import com.arms.jira.jiraissuestatus.model.지라_이슈_상태_데이터_전송_객체;
import com.arms.jira.jiraissuetype.model.지라_이슈_유형_데이터_전송_객체;
import com.arms.jira.jirapriority.model.지라_이슈_우선순위_데이터_전송_객체;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class OnPremiseJiraIssueTest {
    JiraRestClient restClient;

    public String baseUrl = "http://www.313.co.kr/jira";
    public String id = "admin";
    public String pass = "flexjava";
    public String projectKeyOrId = "PHM";
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


    @Test
    @DisplayName("이슈링크_가져오기")
    public void IssueLinkCallTest() throws Exception {
        List<지라_이슈_데이터_전송_객체<String>> result = 이슈링크_가져오기(issueKeyOrId);

        System.out.println("result.toString() = " + result.toString());

    }

    public List<지라_이슈_데이터_전송_객체<String>> 이슈링크_가져오기(String 이슈_키_또는_아이디) throws Exception {

        String jql = "issue in linkedIssues("+이슈_키_또는_아이디+")";

        int startAt = 0;
        int 최대_검색수 = 50;
        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        // 이슈 건수가 1000이 넘을때 이슈 조회를 위한 처리
        List<지라_이슈_데이터_전송_객체<String>> allIssues = new ArrayList<>();
        SearchResult searchResult;

        do {
            searchResult = restClient.getSearchClient()
                    .searchJql(jql, 최대_검색수, startAt, fields)
                    .get();
            for (Issue issue : searchResult.getIssues()) {
                지라_이슈_데이터_전송_객체<String> s = 지라_이슈_데이터_전송_객체로_변환(issue);
                allIssues.add(s);
            }
            startAt += 최대_검색수;
        } while (searchResult.getTotal() > startAt);

        return allIssues;
    }

    @Test
    @DisplayName("서브테스크_가져오기")
    public void SubtaskCallTest() throws ExecutionException, InterruptedException {
        List<지라_이슈_데이터_전송_객체<String>> result = 서브테스크_가져오기(issueKeyOrId);

        System.out.println("result.toString() = " + result.toString());
    }

    List<지라_이슈_데이터_전송_객체<String>> 서브테스크_가져오기(String 이슈_키_또는_아이디) throws ExecutionException, InterruptedException {

        String jql = "parent="+이슈_키_또는_아이디;

        int startAt = 0;
        int 최대_검색수 = 50;
        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        // 이슈 건수가 1000이 넘을때 이슈 조회를 위한 처리
        List<지라_이슈_데이터_전송_객체<String>> allIssues = new ArrayList<>();
        SearchResult searchResult;

        do {
            searchResult = restClient.getSearchClient()
                    .searchJql(jql, 최대_검색수, startAt, fields)
                    .get();
            for (Issue issue : searchResult.getIssues()) {
                지라_이슈_데이터_전송_객체<String> s = 지라_이슈_데이터_전송_객체로_변환(issue);
                allIssues.add(s);
            }
            startAt += 최대_검색수;
        } while (searchResult.getTotal() > startAt);

        return allIssues;
    }

    private 지라_이슈_데이터_전송_객체<String> 지라_이슈_데이터_전송_객체로_변환(Issue 지라_이슈) {

        지라_이슈_데이터_전송_객체 반환할_지라_이슈_데이터= new 지라_이슈_데이터_전송_객체();
        지라_이슈_필드_데이터_전송_객체 지라_이슈_필드_데이터 = new 지라_이슈_필드_데이터_전송_객체();

        // 프로젝트 초기화
        지라_이슈_필드_데이터_전송_객체.프로젝트 프로젝트 = 지라_이슈_필드_데이터_전송_객체.프로젝트.builder().build();

        // 보고자 초기화
        지라_이슈_필드_데이터_전송_객체.보고자 보고자 = 지라_이슈_필드_데이터_전송_객체.보고자.builder().build();

        // 담당자 초기화
        지라_이슈_필드_데이터_전송_객체.담당자 담당자 = 지라_이슈_필드_데이터_전송_객체.담당자.builder().build();

        // 연결된 이슈 초기화
        지라_이슈_데이터_전송_객체 내부_연결_이슈 = new 지라_이슈_데이터_전송_객체();
        지라_이슈_데이터_전송_객체 외부_연결_이슈 = new 지라_이슈_데이터_전송_객체();

        지라_이슈_필드_데이터_전송_객체.연결된_이슈 연결된_이슈 = new 지라_이슈_필드_데이터_전송_객체.연결된_이슈();
        연결된_이슈.setInwardIssue(내부_연결_이슈);
        연결된_이슈.setOutwardIssue(외부_연결_이슈);
        List<지라_이슈_필드_데이터_전송_객체.연결된_이슈> 연결된_이슈_목록 = new ArrayList<>();

        지라_이슈_필드_데이터.setProject(프로젝트);
        지라_이슈_필드_데이터.setReporter(보고자);
        지라_이슈_필드_데이터.setAssignee(담당자);
        지라_이슈_필드_데이터.setIssuelinks(연결된_이슈_목록);


        반환할_지라_이슈_데이터.setFields(지라_이슈_필드_데이터);



        반환할_지라_이슈_데이터.setId(지라_이슈.getId().toString());
        반환할_지라_이슈_데이터.setKey(지라_이슈.getKey());
        반환할_지라_이슈_데이터.setSelf(지라_이슈.getSelf().toString());

        // 필드 하위 프로젝트
        반환할_지라_이슈_데이터.getFields().getProject().setSelf(지라_이슈.getProject().getSelf().toString());
        반환할_지라_이슈_데이터.getFields().getProject().setId(String.valueOf(지라_이슈.getProject().getId()));
        반환할_지라_이슈_데이터.getFields().getProject().setKey(지라_이슈.getProject().getKey());
        반환할_지라_이슈_데이터.getFields().getProject().setName(지라_이슈.getProject().getName());

        // 필드 하위 이슈 타입   지라_이슈_유형_데이터_전송_객체
        if(지라_이슈.getIssueType()!= null){
            지라_이슈_유형_데이터_전송_객체 이슈_유형 =new 지라_이슈_유형_데이터_전송_객체();
            지라_이슈_필드_데이터.setIssuetype(이슈_유형);
            반환할_지라_이슈_데이터.setFields(지라_이슈_필드_데이터);

            String 이슈_유형_주소 = String.valueOf(지라_이슈.getIssueType().getSelf());
            String 이슈_유형_아이디 = String.valueOf(지라_이슈.getIssueType().getId());
            String 이슈_유형_이름 =지라_이슈.getIssueType().getName();

            반환할_지라_이슈_데이터.getFields().getIssuetype().setId(이슈_유형_아이디);
            반환할_지라_이슈_데이터.getFields().getIssuetype().setName(이슈_유형_이름);
            반환할_지라_이슈_데이터.getFields().getIssuetype().setSelf(이슈_유형_주소);
        }


        // 이슈 summary
        반환할_지라_이슈_데이터.getFields().setSummary(지라_이슈.getSummary());

        // 이슈 description
        반환할_지라_이슈_데이터.getFields().setDescription(지라_이슈.getDescription());

        // 이슈 보고자
        반환할_지라_이슈_데이터.getFields().getReporter().setName(지라_이슈.getReporter().getName());
        반환할_지라_이슈_데이터.getFields().getReporter().setEmailAddress(지라_이슈.getReporter().getEmailAddress());

        // 이슈 담당자
        반환할_지라_이슈_데이터.getFields().getAssignee().setName(지라_이슈.getAssignee().getName());
        반환할_지라_이슈_데이터.getFields().getAssignee().setEmailAddress(지라_이슈.getAssignee().getEmailAddress());

        // 이슈 라벨
        Set<String> 지라_라벨 = 지라_이슈.getLabels(); //HashSet 반환
        if (지라_라벨 != null) {
            List<String> 라벨_목록 = new ArrayList<>(지라_라벨);
            반환할_지라_이슈_데이터.getFields().setLabels(라벨_목록);
        } else {
            반환할_지라_이슈_데이터.getFields().setLabels(Collections.emptyList());
        }


        // 이슈 링크
        List<IssueLink> 연결된_이슈_리스트= new ArrayList<>((Collection) 지라_이슈.getIssueLinks());
        for (IssueLink 연결된_이슈_항목: 연결된_이슈_리스트) {

            String direction = String.valueOf(연결된_이슈_항목.getIssueLinkType().getDirection());
            String targetIssueKey = 연결된_이슈_항목.getTargetIssueKey();
            String self = String.valueOf(연결된_이슈_항목.getTargetIssueUri());
            String[] parts = self.split("/");
            String id = parts[parts.length - 1];

            지라_이슈_필드_데이터_전송_객체.연결된_이슈 연결된_이슈_필드 = new 지라_이슈_필드_데이터_전송_객체.연결된_이슈();
            지라_이슈_데이터_전송_객체 내부_연결_이슈_객체 = new 지라_이슈_데이터_전송_객체();
            지라_이슈_데이터_전송_객체 외부_연결_이슈_객체  = new 지라_이슈_데이터_전송_객체();

            연결된_이슈_필드.setInwardIssue(내부_연결_이슈_객체);
            연결된_이슈_필드.setOutwardIssue(외부_연결_이슈_객체);


            if(direction.equals("INBOUND")){
                //로그.info("direction   "+direction+"   targetIssueKey   "+targetIssueKey);
                내부_연결_이슈_객체.setKey(targetIssueKey);
                내부_연결_이슈_객체.setSelf(self);
                내부_연결_이슈_객체.setId(id);
            }
            else if(direction.equals("OUTBOUND")){
                //로그.info("direction   "+direction+"   targetIssueKey   "+targetIssueKey);
                외부_연결_이슈_객체.setKey(targetIssueKey);
                외부_연결_이슈_객체.setSelf(self);
                외부_연결_이슈_객체.setId(id);
            }

            반환할_지라_이슈_데이터.getFields().getIssuelinks().add(연결된_이슈_필드);

        }

        // 서브 테스크
        Iterable<Subtask> 지라_서버_서브테스크_목록 = 지라_이슈.getSubtasks();
        List<Subtask> 서브테스크_목록 = new ArrayList<>();
        for (Subtask 지라_서버_서브테스크 : 지라_서버_서브테스크_목록) {
            서브테스크_목록.add(지라_서버_서브테스크);
        }
        for(Subtask 서브테스크 : 서브테스크_목록){

            String 서브테스크_키 = 서브테스크.getIssueKey();
            String 서브테스크_주소 = String.valueOf(서브테스크.getIssueUri());
            String[] 서브테스크_주소_배열= 서브테스크_주소.split("/");
            String 서브테스크_아이디 = 서브테스크_주소_배열[서브테스크_주소_배열.length - 1];

            String 서브테스크_이슈타입_주소 = String.valueOf(서브테스크.getIssueType().getSelf());
            String[] 서브테스크_이슈타입_주소_배열 = 서브테스크_이슈타입_주소.split("/");
            String 서브테스크_이슈타입_아이디 = 서브테스크_이슈타입_주소_배열[서브테스크_이슈타입_주소_배열.length - 1];
            String 서브테스크_이슈타입_이름 = 서브테스크.getIssueType().getName();

            String 서브테스크_요약 = 서브테스크.getSummary();

            String 서브테스크_상태_주소 = String.valueOf(서브테스크.getStatus().getSelf());
            String 서브테스크_상태_이름 = 서브테스크.getStatus().getName();
            String[] 서브테스크_상태_주소_목록 = 서브테스크_상태_주소.split("/");
            String 서브테스크_상태_아이디 = 서브테스크_상태_주소_목록[서브테스크_상태_주소_목록.length - 1];
            String 서브테스크_상태_설명 = 서브테스크.getStatus().getDescription();

            지라_이슈_데이터_전송_객체 서브테스크_객체 = new 지라_이슈_데이터_전송_객체();
            지라_이슈_필드_데이터_전송_객체 서브테스크_필드_객체 = new 지라_이슈_필드_데이터_전송_객체();

            서브테스크_객체.setFields(서브테스크_필드_객체);
            서브테스크_객체.getFields().setIssuetype(new 지라_이슈_유형_데이터_전송_객체());
            서브테스크_객체.getFields().setPriority(new 지라_이슈_우선순위_데이터_전송_객체());
            서브테스크_객체.getFields().setStatus(new 지라_이슈_상태_데이터_전송_객체());

            서브테스크_객체.setId(서브테스크_아이디);
            서브테스크_객체.setKey(서브테스크_키);
            서브테스크_객체.setSelf(서브테스크_주소);

            서브테스크_객체.getFields().getIssuetype().setSelf(서브테스크_이슈타입_주소);
            서브테스크_객체.getFields().getIssuetype().setId(서브테스크_이슈타입_아이디);
            서브테스크_객체.getFields().getIssuetype().setName(서브테스크_이슈타입_이름);

            서브테스크_객체.getFields().setSummary(서브테스크_요약);

            서브테스크_객체.getFields().getStatus().setId(서브테스크_상태_아이디);
            서브테스크_객체.getFields().getStatus().setName(서브테스크_상태_이름);
            서브테스크_객체.getFields().getStatus().setDescription(서브테스크_상태_설명);
            서브테스크_객체.getFields().getStatus().setSelf(서브테스크_상태_주소);

            if (반환할_지라_이슈_데이터.getFields().getSubtasks() == null) {
                반환할_지라_이슈_데이터.getFields().setSubtasks(new ArrayList<>());
            }
            반환할_지라_이슈_데이터.getFields().getSubtasks().add(서브테스크_객체);
        }

        // 우선 순위
        if(지라_이슈.getPriority() != null) {
            지라_이슈_우선순위_데이터_전송_객체 이슈_우선순위 = new 지라_이슈_우선순위_데이터_전송_객체();
            지라_이슈_필드_데이터.setPriority(이슈_우선순위);
            반환할_지라_이슈_데이터.setFields(지라_이슈_필드_데이터);

            String 이슈_우선순위_이이디 = String.valueOf(지라_이슈.getPriority().getId());
            String 이슈_우선순위_이름 = 지라_이슈.getPriority().getName();
            String 이슈_우선순위_주소 = String.valueOf(지라_이슈.getPriority().getSelf());

            반환할_지라_이슈_데이터.getFields().getPriority().setSelf(이슈_우선순위_주소);
            반환할_지라_이슈_데이터.getFields().getPriority().setId(이슈_우선순위_이이디);
            반환할_지라_이슈_데이터.getFields().getPriority().setName(이슈_우선순위_이름);
        }
        // 상태 값
        if(지라_이슈.getStatus() != null){
            지라_이슈_상태_데이터_전송_객체 이슈_상태 =new 지라_이슈_상태_데이터_전송_객체();
            지라_이슈_필드_데이터.setStatus(이슈_상태);
            반환할_지라_이슈_데이터.setFields(지라_이슈_필드_데이터);

            String 이슈_상태_아이디 = String.valueOf(지라_이슈.getStatus().getId());
            String 이슈_상태_이름 = 지라_이슈.getStatus().getName();
            String 이슈_상태_설명 =  지라_이슈.getStatus().getDescription();
            String 이슈_상태_주소 = String.valueOf(지라_이슈.getStatus().getSelf());

            반환할_지라_이슈_데이터.getFields().getStatus().setId(이슈_상태_아이디);
            반환할_지라_이슈_데이터.getFields().getStatus().setName(이슈_상태_이름);
            반환할_지라_이슈_데이터.getFields().getStatus().setDescription(이슈_상태_설명);
            반환할_지라_이슈_데이터.getFields().getStatus().setSelf(이슈_상태_주소);
        }
        // 해결책
        if(지라_이슈.getResolution()!= null){
            지라_이슈_해결책_데이터_전송_객체 이슈_해결책 =new 지라_이슈_해결책_데이터_전송_객체();
            지라_이슈_필드_데이터.setResolution(이슈_해결책);
            반환할_지라_이슈_데이터.setFields(지라_이슈_필드_데이터);

            String 이슈_해결책_아이디 = String.valueOf(지라_이슈.getResolution().getId());
            String 이슈_해결책_주소 = String.valueOf(지라_이슈.getResolution().getSelf());
            String 이슈_해결책_이름 =지라_이슈.getResolution().getName();
            String 이슈_해결책_설명 = 지라_이슈.getResolution().getDescription();

            반환할_지라_이슈_데이터.getFields().getResolution().setDescription(이슈_해결책_설명);
            반환할_지라_이슈_데이터.getFields().getResolution().setId(이슈_해결책_아이디);
            반환할_지라_이슈_데이터.getFields().getResolution().setName(이슈_해결책_이름);
            반환할_지라_이슈_데이터.getFields().getResolution().setSelf(이슈_해결책_주소);
        }

        return 반환할_지라_이슈_데이터;
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
