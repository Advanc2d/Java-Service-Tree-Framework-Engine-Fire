package com.arms.jira.jiraissue.strategy;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.jiraissue.dao.지라_이슈_저장소;
import com.arms.jira.jiraissue.model.지라_이슈_데이터_전송_객체;
import com.arms.jira.jiraissue.model.지라_이슈_생성_데이터_전송_객체;
import com.arms.jira.jiraissue.model.지라_이슈_엔티티;
import com.arms.jira.jiraissue.model.지라_이슈_필드_데이터_전송_객체;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class 온프레미스_지라_이슈_전략 implements 지라_이슈_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private 지라_이슈_저장소 지라_이슈_저장소;

    @Override
    public List<지라_이슈_데이터_전송_객체> 이슈_전체_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {
        return null;
    }

    @Override
    public 지라_이슈_데이터_전송_객체 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {
        return null;
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

        지라_이슈_필드_데이터_전송_객체 필드_데이터 = 지라_이슈_생성_데이터_전송_객체.getFields();
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
            지라_이슈_필드_데이터_전송_객체 필드_데이터 = 지라_이슈_생성_데이터_전송_객체.getFields();
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

    @Override
    public Map<String, Object> 이슈_연결_링크_및_서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {
        return null;
    }
}
