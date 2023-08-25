package com.arms.jira.jiraissue.strategy;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.jiraissue.dao.지라_이슈_저장소;
import com.arms.jira.jiraissue.model.*;
import com.arms.jira.jiraissueresolution.model.지라_이슈_해결책_데이터_전송_객체;
import com.arms.jira.jiraissuestatus.model.지라_이슈_상태_데이터_전송_객체;
import com.arms.jira.jiraissuetype.model.지라_이슈_유형_데이터_전송_객체;
import com.arms.jira.jirapriority.model.지라_이슈_우선순위_데이터_전송_객체;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class 온프레미스_지라_이슈_전략<T> implements 지라_이슈_전략<T> {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private 지라_이슈_저장소 지라_이슈_저장소;

    @Override
    public List<지라_이슈_데이터_전송_객체<T>> 이슈_전체_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) throws Exception {
        JiraInfoDTO info = jiraInfo.checkInfo(연결_아이디);

        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                info.getUserId(),
                info.getPasswordOrToken());

        String 조회대상_프로젝트 = "project = " + 프로젝트_키_또는_아이디;
        int 검색_끝_지점 = 50;
        int 검색_시작_지점 = 0;
        Set<String> 필드 = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        // 이슈 건수가 1000이 넘을때 이슈 조회를 위한 처리
        List<지라_이슈_데이터_전송_객체<T>> 프로젝트_이슈_목록 = new ArrayList<>();
        while (true) {
            SearchResult 프로젝트_이슈_검색결과 = restClient.getSearchClient()
                    .searchJql(조회대상_프로젝트, 검색_끝_지점, 검색_시작_지점, 필드)
                    .claim();

            for (Issue 지라_이슈 : 프로젝트_이슈_검색결과.getIssues()) {
                프로젝트_이슈_목록.add(지라_이슈_데이터_전송_객체로_변환(지라_이슈));
            }

            if (프로젝트_이슈_목록.size() >= 프로젝트_이슈_검색결과.getTotal()) {
                break;
            }

            검색_시작_지점 += 검색_끝_지점;
        }
        return 프로젝트_이슈_목록;

    }

    @Override
    public 지라_이슈_데이터_전송_객체 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception {

        로그.info("온프레미스 지라 이슈 생성하기");

        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(연결정보.getUri(),
                연결정보.getUserId(),
                연결정보.getPasswordOrToken());
        try {
            Issue 지라_이슈 = restClient.getIssueClient().getIssue(이슈_키_또는_아이디).claim();
            return 지라_이슈_데이터_전송_객체로_변환(지라_이슈);
        }catch (RestClientException e) {
            로그.info("이슈 조회시 오류가 발생하였습니다.");
            throw new RuntimeException("이슈 조회시 오류가 발생하였습니다.");
        }
    }

    /* ***
     * 수정사항: null 체크하여 에러 처리 필요
     *** */
    @Override
    public 지라_이슈_데이터_전송_객체 이슈_생성하기(Long 연결_아이디, 지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) throws Exception {

        로그.info("온프레미스 지라 이슈 생성하기");
        
        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(연결정보.getUri(),
                                                                         연결정보.getUserId(),
                                                                         연결정보.getPasswordOrToken());

//        if (지라_이슈_생성_데이터_전송_객체 == null) {
//            로그.info("생성할 이슈 데이터가 없습니다.");
//            /* ***
//             * 수정사항: 에러 처리 필요
//             *** */
//            return null;
//        }

        지라_이슈_필드_데이터_전송_객체<String> 필드_데이터 = 지라_이슈_생성_데이터_전송_객체.getFields();
        if (필드_데이터 == null) {
            /* ***
             * 수정사항: 에러 처리 필요
             *** */
            return null;
        }

        String 프로젝트키 = null;
        Long 이슈유형아이디 = null;
        String 제목 = null;
        String 내용 = null;
        String 보고자 = null;
        String 담당자 = null;
        Long 우선순위아이디 = null;

        if (필드_데이터.getProject() != null) {
            프로젝트키 = 필드_데이터.getProject().getKey();
        }
        if (필드_데이터.getIssuetype() != null) {
            이슈유형아이디 = Long.valueOf(필드_데이터.getIssuetype().getId());
        }
        if (필드_데이터.getSummary() != null) {
            제목 = 필드_데이터.getSummary();
        }
        if (필드_데이터.getDescription() != null) {
            내용 = 필드_데이터.getDescription();
        }
        if (필드_데이터.getReporter() != null) {
            보고자 = 필드_데이터.getReporter().getName();
        }
        if (필드_데이터.getAssignee() != null) {
            담당자 = 필드_데이터.getAssignee().getName();
        }
        if (필드_데이터.getPriority() != null) {
            우선순위아이디 = Long.valueOf(필드_데이터.getPriority().getId());
        }

        IssueInputBuilder 입력_생성 = new IssueInputBuilder(프로젝트키, 이슈유형아이디, 제목);
        if (입력_생성 == null) {
            /* ***
             * 수정사항: 에러 처리 필요
             *** */
            return null;
        }
        입력_생성.setDescription(내용);
        if (보고자 != null) {
            입력_생성.setReporterName(보고자);
        }
        if (담당자 != null) {
            입력_생성.setAssigneeName(담당자);
        }
        if (우선순위아이디 != null) {
            입력_생성.setPriorityId(우선순위아이디);
        }
        IssueInput 입력_데이터 = 입력_생성.build();

        BasicIssue 생성된_이슈 = restClient.getIssueClient().createIssue(입력_데이터).claim();
        if (생성된_이슈 == null) {
            로그.info("이슈 생성에 실패하였습니다.");
            /* ***
             * 수정사항: 에러 처리 필요
             *** */
            return null;
        }

        지라_이슈_데이터_전송_객체 반환할_지라_이슈_데이터_전송_객체 = new 지라_이슈_데이터_전송_객체();
        반환할_지라_이슈_데이터_전송_객체.setId(생성된_이슈.getId().toString());
        반환할_지라_이슈_데이터_전송_객체.setKey(생성된_이슈.getKey());
        반환할_지라_이슈_데이터_전송_객체.setSelf(생성된_이슈.getSelf().toString());

        // DB 저장
        지라_이슈_엔티티 지라_이슈_엔티티 = modelMapper.map(반환할_지라_이슈_데이터_전송_객체, 지라_이슈_엔티티.class);
        지라_이슈_엔티티.setConnectId(연결_아이디);
        지라_이슈_저장소.save(지라_이슈_엔티티);

        return 반환할_지라_이슈_데이터_전송_객체;
    }

    @Override
    public Map<String, Object> 이슈_수정하기(Long 연결_아이디, String 이슈_키_또는_아이디, 지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) throws Exception {

        로그.info("온프레미스 지라 이슈 수정하기");

        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(연결정보.getUri(),
                                                                         연결정보.getUserId(),
                                                                         연결정보.getPasswordOrToken());

        Map<String, Object> 결과 = new HashMap<>();

        try {
            지라_이슈_필드_데이터_전송_객체<String> 필드_데이터 = 지라_이슈_생성_데이터_전송_객체.getFields();
            if (필드_데이터.getProject() != null || 필드_데이터.getIssuetype() != null || 필드_데이터.getReporter() != null ||
                필드_데이터.getAssignee() != null || 필드_데이터.getIssuelinks() != null || 필드_데이터.getSubtasks() != null ||
                필드_데이터.getPriority() != null || 필드_데이터.getStatus() != null || 필드_데이터.getResolution() != null) {

                로그.info("입력 값에 수정할 수 없는 필드가 있습니다.");

                결과.put("이슈 수정", "실패");
                결과.put("에러 메시지", "수정할 수 없는 필드가 포함됨");

                return 결과;
            }

            IssueInputBuilder 입력_생성 = new IssueInputBuilder();

            if (필드_데이터.getSummary() != null) {
                입력_생성.setSummary(필드_데이터.getSummary());
            }

            if (필드_데이터.getDescription() != null) {
                입력_생성.setDescription(필드_데이터.getDescription());
            }

            if (필드_데이터.getLabels() != null) {
                입력_생성.setFieldValue("labels", 필드_데이터.getLabels());
            }

            IssueInput 수정_데이터 = 입력_생성.build();
            restClient.getIssueClient().updateIssue(이슈_키_또는_아이디, 수정_데이터).claim();
            결과.put("success", true);
            결과.put("message", "이슈 수정 성공");

            return 결과;

        } catch (Exception e) {
            결과.put("success", false);
            결과.put("message", "이슈 수정 실패 : " + e.getMessage());
        }

        return 결과;
    }

    @Override
    public Map<String, Object> 이슈_삭제_라벨_처리하기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception {

        로그.info("온프레미스 지라 이슈 삭제 라벨 처리하기");

        Map<String, Object> 반환할_결과맵 = new HashMap<String, Object>();
        String 삭제_라벨링 = "이슈_삭제_라벨_처리";

        지라_이슈_필드_데이터_전송_객체 필드_데이터_전송_객체 = new 지라_이슈_필드_데이터_전송_객체();
        필드_데이터_전송_객체.setLabels(List.of(삭제_라벨링));

        지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체 = new 지라_이슈_생성_데이터_전송_객체();
        지라_이슈_생성_데이터_전송_객체.setFields(필드_데이터_전송_객체);

        Map<String, Object> 라벨_처리_결과맵 = 이슈_수정하기(연결_아이디, 이슈_키_또는_아이디, 지라_이슈_생성_데이터_전송_객체);

        if (!((Boolean) 라벨_처리_결과맵.get("success"))) {
            반환할_결과맵.put("success", false);
            반환할_결과맵.put("message", "이슈 라벨 닫기 처리 실패 : " + 라벨_처리_결과맵.toString());
        }
        else {
            반환할_결과맵.put("success", true);
            반환할_결과맵.put("message", "이슈 라벨 닫기 처리 성공");
        }

        return 반환할_결과맵;

    }

    private 지라_이슈_데이터_전송_객체 지라_이슈_데이터_전송_객체로_변환(Issue 지라_이슈) {

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
}
