package com.engine.jira.cloud;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueDTO;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueInputDTO;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueSearchDTO;
import com.arms.jira.cloud.jiraissue.model.FieldsDTO;
import com.arms.jira.cloud.jiraissue.model.FieldsDTO.IssueLink;
import com.arms.jira.cloud.jiraissuepriority.model.Priority;
import com.arms.jira.cloud.jiraissuepriority.model.PrioritySearchDTO;
import com.arms.jira.cloud.jiraissueresolution.model.Resolution;
import com.arms.jira.cloud.jiraissueresolution.model.ResolutionSearchDTO;
import com.arms.jira.jiraissue.model.지라_이슈_데이터_전송_객체;
import com.arms.jira.jiraissue.model.클라우드_지라_이슈_조회_데이터_전송_객체;
import com.arms.jira.jiraissue.model.클라우드_지라_이슈_필드_데이터_전송_객체;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CloudJiraIssueTest {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    WebClient webClient;

    ObjectMapper objectMapper = new ObjectMapper();

    public String baseUrl = "https://advanc2d.atlassian.net";
    public String id = "gkfn185@gmail.com";
    public String pass = "ATATT3xFfGF0OhyPJU1DlcjJmtsZBXsuXPmet-VBfz07AN6R_vGsV6rOeO6loKVV7iEBsMsmW0WPO4vpPokpcRR_QMrpHi9VJtWdLDLKrhG27j6aGFCeQh5_0sDjWjK45jcJsmQ606vB2Mt9ZYfSAdrRRjlUHceqBiU_Mq7--spJIpAOy7Wi0w4=0122341F";

    public String projectKeyOrId = "WM";
    public String issueKeyOrId = projectKeyOrId + "-3";

    // public String fieldsParam = "&fields=-subtasks,-issuelinks,-description";

    public String fieldsParam = "&fields=issuetype,project,resolutiondate,watches,created,priority,labels,versions,assignee,status,creator,reporter";

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
    @DisplayName("이슈 라벨로 닫기 처리 테스트")
    public void IssueClosedLabelTest() {
        String closedLabel = "closeLabel";

        FieldsDTO fieldsDTO = new FieldsDTO();
        fieldsDTO.setLabels(List.of(closedLabel));

        CloudJiraIssueInputDTO cloudJiraIssueInputDTO = new CloudJiraIssueInputDTO();
        cloudJiraIssueInputDTO.setFields(fieldsDTO);

        Map<String, Object> addLabelResult = updateIssue(issueKeyOrId, cloudJiraIssueInputDTO);

        Assertions.assertThat(addLabelResult.get("success")).isEqualTo(true);

    }

    public Map<String,Object> updateIssue(String issueKeyOrId, CloudJiraIssueInputDTO cloudJiraIssueInputDTO) {

        String endpoint = "/rest/api/3/issue/" + issueKeyOrId;

        Mono<ResponseEntity<Void>> response = webClient.put()
                .uri(endpoint)
                .body(BodyInserters.fromValue(cloudJiraIssueInputDTO))
                .retrieve()
                .toEntity(Void.class);

        Optional<Boolean> responseResult = response.map(entity -> entity.getStatusCode() == HttpStatus.NO_CONTENT) // 결과가 204인가 확인
                .blockOptional();
        Map<String,Object> result = new HashMap<String,Object>();
        boolean isSuccess = false;

        if (responseResult.isPresent()) {
            if (responseResult.get()) {
                // PUT 호출이 HTTP 204로 성공했습니다.
                isSuccess = true;
                result.put("success", isSuccess);
                result.put("message", "이슈 수정 성공");

                return result;
            }
        }

        result.put("success", isSuccess);
        result.put("message", "이슈 수정 실패");

        return result;

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

    @Test
    @DisplayName("전체 우선순위 조회 테스트")
    public void IssuePriorityCallTest() {
        PrioritySearchDTO priorities = getPriority();

        System.out.println(priorities.toString());

        Assertions.assertThat(priorities.getTotal()).isEqualTo(5);
    }

    public PrioritySearchDTO getPriority() {
        int maxResult = 50;
        int startAt = 0;
        int index= 1;
        boolean checkLast = false;

        List<Priority> values = new ArrayList<Priority>();
        PrioritySearchDTO result = null;

        while(!checkLast) {

            String endpoint = "/rest/api/3/priority/search?maxResults="+ maxResult + "&startAt=" + startAt;

            PrioritySearchDTO priorities = webClient.get()
                    .uri(endpoint)
                    .retrieve()
                    .bodyToMono(PrioritySearchDTO.class).block();

            values.addAll(priorities.getValues());

            if (priorities.getTotal() == values.size()) {
                result = priorities;
                result.setValues(null);

                checkLast = true;
            }
            else {
                startAt = maxResult * index;
                index++;
            }
        }

        result.setValues(values);

        return result;
    }

    @Test
    @DisplayName("전체 해결책 조회 테스트")
    public void IssueResolutionCallTest() {
        ResolutionSearchDTO resolutions = getResoltuionList();

        System.out.println(resolutions.toString());

        Assertions.assertThat(resolutions.getTotal()).isEqualTo(5);
    }

    public ResolutionSearchDTO getResoltuionList() {
        int maxResult = 50;
        int startAt = 0;
        int index= 1;
        boolean checkLast = false;

        List<Resolution> values = new ArrayList<Resolution>();
        ResolutionSearchDTO result = null;

        while(!checkLast) {

            String endpoint = "/rest/api/3/resolution/search?maxResults="+ maxResult + "&startAt=" + startAt;

            ResolutionSearchDTO resolutions = webClient.get()
                    .uri(endpoint)
                    .retrieve()
                    .bodyToMono(ResolutionSearchDTO.class).block();

            values.addAll(resolutions.getValues());

            if (resolutions.getTotal() == values.size()) {
                result = resolutions;
                result.setValues(null);

                checkLast = true;
            }
            else {
                startAt = maxResult * index;
                index++;
            }
        }

        result.setValues(values);

        return result;
    }

    /* ***
    *  스트림 처리 부분
    *** */
    @Test
    @DisplayName("이슈 조회 스트림 처리 후 DTO 변경하기")
    public void IssueCallTest() throws JsonProcessingException {
        String endpoint = "/rest/api/3/search?jql=project=" + projectKeyOrId;
//        String endpoint = "/rest/api/3/project/" + projectKeyOrId;
//        webClient.get()
//                .uri(endpoint)
//                .accept(MediaType.APPLICATION_STREAM_JSON)
//                .retrieve()
//                .bodyToMono(지라_이슈_데이터_전송_객체.class)
//                .subscribe(result -> {
//                    System.out.println("result = " + result);
//                    // 추가적인 로직...
//                });

        String result = get(webClient, endpoint)
                        .reduce("", (s1, s2) -> s1 + s2)
                        .block();
        List<지라_이슈_데이터_전송_객체> list =
                objectMapper.readValue(result, List.class);

        System.out.println("result = " + list.toString());
    }

    public Flux<String> get(WebClient webClient, String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(dataBuffer -> {
                    System.out.println(dataBuffer.readableByteCount());
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    System.out.println("bytes = " + bytes.length);
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                });
    }

    public <T> Flux<?> callApiAndProcessResponse(String apiUrl, Class<T> dtoClass, boolean isList) {
        return get(webClient, apiUrl)
                .map(chunk -> convertToDtoOrListOfDto(chunk, dtoClass, isList)); // 각 청크를 DTO 또는 List<DTO>로 변환
    }

    private <T> Object convertToDtoOrListOfDto(String chunk, Class<T> dtoClass, boolean isList) {

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            if (isList) {
                JavaType javaType =
                        objectMapper.getTypeFactory().constructCollectionType(List.class,
                                dtoClass);

                // JSON 문자열을 List<DTO> 객체로 변환
                return objectMapper.readValue(chunk.trim(), javaType);
            } else {

                // JSON 문자열을 DTO 객체로 변환
                return objectMapper.readValue(chunk.trim(), dtoClass);
            }

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to convert chunk to DTO or List of DTO due to JsonProcessingException: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert chunk to DTO or List of DTO: " + e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("이슈 조회 스트림 처리 테스트")
    public void IssueStreamCallTest() throws IOException, InterruptedException {

        String endpoint = "/rest/api/3/issue/" + issueKeyOrId;

        InputStream inputStream = getResponseAsInputStream(webClient, endpoint);
        String content = readContentFromPipedInputStream((PipedInputStream) inputStream);
        System.out.println(content);
        로그.info("response content: \n{}", content.replace("}", "}\n"));
    }

    public InputStream getResponseAsInputStream(WebClient client, String url) throws IOException, InterruptedException {

        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(1024 * 10);
        pipedInputStream.connect(pipedOutputStream);

        Flux<DataBuffer> body = client.get()
                .uri(url)
                .exchangeToFlux(clientResponse -> {
                    return clientResponse.body(BodyExtractors.toDataBuffers());
                })
                .doOnError(error -> {
                    로그.error("error occurred while reading body", error);
                })
                .doFinally(s -> {
                    try {
                        pipedOutputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .doOnCancel(() -> {
                    로그.error("Get request is cancelled");
                });

        DataBufferUtils.write(body, pipedOutputStream)
                .log("Writing to output buffer")
                .subscribe();

        return pipedInputStream;
    }

    private String readContentFromPipedInputStream(PipedInputStream stream) throws IOException {
        StringBuffer contentStringBuffer = new StringBuffer();
        try {
            Thread pipeReader = new Thread(() -> {
                try {
                    contentStringBuffer.append(readContent(stream));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            pipeReader.start();
            pipeReader.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stream.close();
        }

        return String.valueOf(contentStringBuffer);
    }

    private String readContent(InputStream stream) throws IOException {
        StringBuffer contentStringBuffer = new StringBuffer();
        byte[] tmp = new byte[stream.available()];
        int byteCount = stream.read(tmp, 0, tmp.length);
        로그.info(String.format("read %d bytes from the stream\n", byteCount));
        contentStringBuffer.append(new String(tmp));
        return String.valueOf(contentStringBuffer);
    }

    @Test
    @DisplayName("이슈링크_가져오기")
    public void IssueLinkCallTest() {
        List<지라_이슈_데이터_전송_객체<클라우드_지라_이슈_필드_데이터_전송_객체.내용>> result = 이슈링크_가져오기(issueKeyOrId);

        System.out.println("result.toString() = " + result.toString());

    }

    List<지라_이슈_데이터_전송_객체<클라우드_지라_이슈_필드_데이터_전송_객체.내용>> 이슈링크_가져오기(String 이슈_키_또는_아이디) {
        int 검색_시작_지점 = 0;
        int 검색_최대_개수 = 50;
        boolean isLast = false;

        List<지라_이슈_데이터_전송_객체<클라우드_지라_이슈_필드_데이터_전송_객체.내용>> 이슈링크_목록 = new ArrayList<>(); // 이슈 저장

        while (!isLast) {
            String endpoint = "/rest/api/3/search?jql=issue in linkedIssues(" + 이슈_키_또는_아이디 + ")" +fieldsParam
                    + "&startAt=" + 검색_시작_지점 + "&maxResults=" + 검색_최대_개수;

            클라우드_지라_이슈_조회_데이터_전송_객체<클라우드_지라_이슈_필드_데이터_전송_객체.내용> 서브테스크_조회결과
                    = CloudJiraUtils.get(webClient, endpoint, 클라우드_지라_이슈_조회_데이터_전송_객체.class).block();

            이슈링크_목록.addAll(서브테스크_조회결과.getIssues());

            if (서브테스크_조회결과.getTotal() == 이슈링크_목록.size()) {
                isLast = true;
            }else{
                검색_시작_지점 += 검색_최대_개수;
            }
        }

        System.out.println(이슈링크_목록.toString());

        return 이슈링크_목록;
    }

    @Test
    @DisplayName("서브테스크_가져오기")
    public void SubtaskCallTest() {
        List<지라_이슈_데이터_전송_객체<클라우드_지라_이슈_필드_데이터_전송_객체.내용>> result = 서브테스크_가져오기(issueKeyOrId);

        System.out.println("result.toString() = " + result.toString());
    }

    List<지라_이슈_데이터_전송_객체<클라우드_지라_이슈_필드_데이터_전송_객체.내용>> 서브테스크_가져오기(String 이슈_키_또는_아이디) {

        int 검색_시작_지점 = 0;
        int 검색_최대_개수 = 50;
        boolean isLast = false;

        List<지라_이슈_데이터_전송_객체<클라우드_지라_이슈_필드_데이터_전송_객체.내용>> 서브테스크_목록 = new ArrayList<>(); // 이슈 저장

        while (!isLast) {
            String endpoint = "/rest/api/3/search?jql=parent="+ 이슈_키_또는_아이디 + fieldsParam
                            + "&startAt=" + 검색_시작_지점 + "&maxResults=" + 검색_최대_개수;

            클라우드_지라_이슈_조회_데이터_전송_객체<클라우드_지라_이슈_필드_데이터_전송_객체.내용> 서브테스크_조회결과
                    = CloudJiraUtils.get(webClient, endpoint, 클라우드_지라_이슈_조회_데이터_전송_객체.class).block();

            서브테스크_목록.addAll(서브테스크_조회결과.getIssues());

            if (서브테스크_조회결과.getTotal() == 서브테스크_목록.size()) {
                isLast = true;
            }else{
                검색_시작_지점 += 검색_최대_개수;
            }
        }

        System.out.println(서브테스크_목록.toString());

        return 서브테스크_목록;
    }
}