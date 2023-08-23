package com.arms.jira.jiraissue.strategy;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.jiraissue.dao.지라_이슈_저장소;
import com.arms.jira.jiraissue.model.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Component
public class 클라우드_지라_이슈_전략 implements 지라_이슈_전략 {

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
    public 지라_이슈_데이터_전송_객체 이슈_생성하기(Long 연결_아이디, 지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) {

        로그.info("클라우드 지라 이슈 생성하기");

        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(연결정보.getUri(), 연결정보.getUserId(), 연결정보.getPasswordOrToken());

//        if (지라_이슈_생성_데이터_전송_객체 == null) {
//            로그.info("생성할 이슈 데이터가 없습니다.");
//            /* ***
//             * 수정사항: 에러 처리 필요
//             *** */
//            return null;
//        }

        String endpoint = "/rest/api/3/issue";
        지라_이슈_필드_데이터_전송_객체 필드_데이터 = 지라_이슈_생성_데이터_전송_객체.getFields();
        if (필드_데이터 == null) {
            /* ***
             * 수정사항: 에러 처리 필요
             *** */
            return null;
        }

        클라우드_지라_이슈_생성_데이터_전송_객체 입력_데이터 = new 클라우드_지라_이슈_생성_데이터_전송_객체();
        클라우드_지라_이슈_필드_데이터_전송_객체 클라우드_필드_데이터 = new 클라우드_지라_이슈_필드_데이터_전송_객체();

        if (필드_데이터.getProject() != null) {
            클라우드_필드_데이터.setProject(필드_데이터.getProject());
        }
        if (필드_데이터.getIssuetype() != null) {
            클라우드_필드_데이터.setIssuetype(필드_데이터.getIssuetype());
        }
        if (필드_데이터.getSummary() != null) {
            클라우드_필드_데이터.setSummary(필드_데이터.getSummary());
        }

        if (필드_데이터.getDescription() != null) {
            클라우드_필드_데이터.setDescription(내용_변환(필드_데이터.getDescription()));
        }

        if (필드_데이터.getReporter() != null) {
            클라우드_지라_이슈_필드_데이터_전송_객체.보고자 보고자 = 클라우드_지라_이슈_필드_데이터_전송_객체.보고자.builder()
                                                                                                   .accountId(필드_데이터.getReporter().getName())
                                                                                                   .emailAddress(필드_데이터.getReporter().getEmailAddress())
                                                                                                   .build();

            클라우드_필드_데이터.setReporter(보고자);
        }

        if (필드_데이터.getAssignee() != null) {
            클라우드_지라_이슈_필드_데이터_전송_객체.담당자 담당자 = 클라우드_지라_이슈_필드_데이터_전송_객체.담당자.builder()
                                                                                                   .accountId(필드_데이터.getAssignee().getName())
                                                                                                   .emailAddress(필드_데이터.getAssignee().getEmailAddress())
                                                                                                   .build();

            클라우드_필드_데이터.setAssignee(담당자);
        }

        if (필드_데이터.getPriority() != null) {
            클라우드_필드_데이터.setPriority(필드_데이터.getPriority());
        }

        입력_데이터.setFields(클라우드_필드_데이터);
        로그.info(String.valueOf(입력_데이터));

        지라_이슈_데이터_전송_객체 반환할_지라_이슈_데이터_전송_객체 = CloudJiraUtils.post(webClient, endpoint, 입력_데이터, 지라_이슈_데이터_전송_객체.class).block();
        if (반환할_지라_이슈_데이터_전송_객체 == null) {
            로그.info("이슈 생성에 실패하였습니다.");
            /* ***
             * 수정사항: 에러 처리 필요
             *** */
            return null;
        }

        // DB 저장
        지라_이슈_엔티티 지라_이슈_엔티티 = modelMapper.map(반환할_지라_이슈_데이터_전송_객체, 지라_이슈_엔티티.class);
        지라_이슈_엔티티.setConnectId(연결_아이디);
        지라_이슈_저장소.save(지라_이슈_엔티티);

        return 반환할_지라_이슈_데이터_전송_객체;
    }

    @Override
    public Map<String, Object> 이슈_수정하기(Long 연결_아이디, String 이슈_키_또는_아이디, 지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) {

        로그.info("클라우드 지라 이슈 수정하기");

        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(연결정보.getUri(), 연결정보.getUserId(), 연결정보.getPasswordOrToken());

        String endpoint = "/rest/api/3/issue/" + 이슈_키_또는_아이디;
        Map<String, Object> 결과 = new HashMap<>();

        지라_이슈_필드_데이터_전송_객체 필드_데이터 = 지라_이슈_생성_데이터_전송_객체.getFields();
        if (필드_데이터.getProject() != null || 필드_데이터.getIssuetype() != null || 필드_데이터.getReporter() != null ||
            필드_데이터.getAssignee() != null || 필드_데이터.getIssuelinks() != null || 필드_데이터.getSubtasks() != null ||
            필드_데이터.getPriority() != null || 필드_데이터.getStatus() != null || 필드_데이터.getResolution() != null) {

            로그.info("입력 값에 수정할 수 없는 필드가 있습니다.");

            결과.put("이슈 수정", "실패");
            결과.put("에러 메시지", "수정할 수 없는 필드가 포함");

            return 결과;
        }

        클라우드_지라_이슈_생성_데이터_전송_객체 수정_데이터 = new 클라우드_지라_이슈_생성_데이터_전송_객체();
        클라우드_지라_이슈_필드_데이터_전송_객체 클라우드_필드_데이터 = new 클라우드_지라_이슈_필드_데이터_전송_객체();

        if (필드_데이터.getSummary() != null) {
            클라우드_필드_데이터.setSummary(필드_데이터.getSummary());
        }

        if (필드_데이터.getDescription() != null) {
            클라우드_필드_데이터.setDescription(내용_변환(필드_데이터.getDescription()));
        }

        if (필드_데이터.getLabels() != null) {
            클라우드_필드_데이터.setLabels(필드_데이터.getLabels());
        }

        수정_데이터.setFields(클라우드_필드_데이터);
        Optional<Boolean> 응답_결과 = CloudJiraUtils.executePut(webClient, endpoint, 수정_데이터);

        if (응답_결과.isPresent()) {
            if (응답_결과.get()) {

                결과.put("success", true);
                결과.put("message", "이슈 수정 성공");

                return 결과;
            }
        }
        결과.put("success", false);
        결과.put("message", "이슈 수정 실패");

        return 결과;
    }

    @Override
    public Map<String, Object> 이슈_삭제_라벨_처리하기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("지라 이슈 삭제 라벨 처리하기");

        Map<String, Object> 반환할_결과맵 = new HashMap<String, Object>();

        String 삭제_라벨링 = "이슈_삭제_라벨_처리";

        지라_이슈_필드_데이터_전송_객체 필드_데이터_전송_객체 = new 지라_이슈_필드_데이터_전송_객체();
        필드_데이터_전송_객체.setLabels(List.of(삭제_라벨링));

        지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체 = new 지라_이슈_생성_데이터_전송_객체();
        지라_이슈_생성_데이터_전송_객체.setFields(필드_데이터_전송_객체);

        Map<String, Object> 라벨_처리_결과맵 = 이슈_수정하기(연결_아이디, 이슈_키_또는_아이디, 지라_이슈_생성_데이터_전송_객체);

        if (!((Boolean) 라벨_처리_결과맵.get("success"))) {
            반환할_결과맵.put("success", false);
            반환할_결과맵.put("message", "이슈 라벨 닫기 처리 실패");
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

    public 클라우드_지라_이슈_필드_데이터_전송_객체.내용 내용_변환(String 입력_데이터) {

        클라우드_지라_이슈_필드_데이터_전송_객체.콘텐츠_아이템 콘텐츠_아이템 = 클라우드_지라_이슈_필드_데이터_전송_객체.콘텐츠_아이템.builder()
                .text(입력_데이터)
                .type("text")
                .build();

        List<클라우드_지라_이슈_필드_데이터_전송_객체.콘텐츠_아이템> 콘텐츠_아이템_리스트 = new ArrayList<>();
        콘텐츠_아이템_리스트.add(콘텐츠_아이템);

        클라우드_지라_이슈_필드_데이터_전송_객체.콘텐츠 콘텐츠 = 클라우드_지라_이슈_필드_데이터_전송_객체.콘텐츠.builder()
                .content(콘텐츠_아이템_리스트)
                .type("paragraph")
                .build();

        List<클라우드_지라_이슈_필드_데이터_전송_객체.콘텐츠> 콘텐츠_리스트 = new ArrayList<>();
        콘텐츠_리스트.add(콘텐츠);

        클라우드_지라_이슈_필드_데이터_전송_객체.내용 내용 = 클라우드_지라_이슈_필드_데이터_전송_객체.내용.builder()
                .content(콘텐츠_리스트)
                .type("doc")
                .version(1)
                .build();

        return 내용;
    }

}
