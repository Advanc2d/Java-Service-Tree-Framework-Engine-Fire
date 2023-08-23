package com.arms.jira.jiraissue.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.model.지라_유형_정보;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.jiraissue.model.지라_이슈_데이터_전송_객체;
import com.arms.jira.jiraissue.model.지라_이슈_생성_데이터_전송_객체;
import com.arms.jira.jiraissue.strategy.클라우드_지라_이슈_전략;
import com.arms.jira.jiraissue.strategy.온프레미스_지라_이슈_전략;
import com.arms.jira.jiraissue.strategy.지라_이슈_전략_등록_및_실행;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class 지라_이슈_전략_호출 {

    지라_이슈_전략_등록_및_실행 지라_이슈_전략_등록_및_실행;

    클라우드_지라_이슈_전략 클라우드_지라_이슈_전략;

    온프레미스_지라_이슈_전략 온프레미스_지라_이슈_전략;

    JiraInfo jiraInfo;

    @Autowired
    public 지라_이슈_전략_호출(지라_이슈_전략_등록_및_실행 지라_이슈_전략_등록_및_실행,
                            클라우드_지라_이슈_전략 클라우드_지라_이슈_전략,
                            온프레미스_지라_이슈_전략 온프레미스_지라_이슈_전략,
                            JiraInfo jiraInfo) {

        this.지라_이슈_전략_등록_및_실행 = 지라_이슈_전략_등록_및_실행;
        this.클라우드_지라_이슈_전략 = 클라우드_지라_이슈_전략;
        this.온프레미스_지라_이슈_전략 = 온프레미스_지라_이슈_전략;
        this.jiraInfo = jiraInfo;
    }

    private 지라_이슈_전략_등록_및_실행 지라_이슈_전략_확인(JiraInfoDTO 연결정보) {

        if(연결정보 == null || 연결정보.getType().isEmpty()) {
            return null;
        }

        지라_유형_정보 지라_유형 = 지라_유형_정보.valueOf(연결정보.getType());

        if (지라_유형 == 지라_유형_정보.클라우드) {
            지라_이슈_전략_등록_및_실행.지라_이슈_전략_등록(클라우드_지라_이슈_전략);
        }
        else if (지라_유형 == 지라_유형_정보.온프레미스) {
            지라_이슈_전략_등록_및_실행.지라_이슈_전략_등록(온프레미스_지라_이슈_전략);
        }

        return 지라_이슈_전략_등록_및_실행;
    }

    public List<지라_이슈_데이터_전송_객체> 이슈_전체_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {

        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);

        지라_이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        List<지라_이슈_데이터_전송_객체> 반환할_지라_이슈_데이터_전송_객체_목록
                = 지라_이슈_전략_등록_및_실행.이슈_전체_목록_가져오기(연결_아이디, 프로젝트_키_또는_아이디);

        return 반환할_지라_이슈_데이터_전송_객체_목록;

    }

    public 지라_이슈_데이터_전송_객체 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);

        지라_이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        지라_이슈_데이터_전송_객체 반환할_지라_이슈_데이터_전송_객체
                = 지라_이슈_전략_등록_및_실행.이슈_상세정보_가져오기(연결_아이디, 이슈_키_또는_아이디);

        return 반환할_지라_이슈_데이터_전송_객체;

    }

    public 지라_이슈_데이터_전송_객체 이슈_생성하기(Long 연결_아이디,
                                   지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) throws Exception {

        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);

        지라_이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        지라_이슈_데이터_전송_객체 반환할_지라_이슈_데이터_전송_객체
                = 지라_이슈_전략_등록_및_실행.이슈_생성하기(연결_아이디, 지라_이슈_생성_데이터_전송_객체);

        return 반환할_지라_이슈_데이터_전송_객체;

    }

    public Map<String,Object> 이슈_수정하기(Long 연결_아이디, String 이슈키,
                                      지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) throws Exception {

        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);

        지라_이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        Map<String,Object> 반환할_지라_이슈_데이터_전송_객체
                = 지라_이슈_전략_등록_및_실행.이슈_수정하기(연결_아이디, 이슈키, 지라_이슈_생성_데이터_전송_객체);

        return 반환할_지라_이슈_데이터_전송_객체;

    }

    public Map<String,Object> 이슈_삭제_라벨_처리하기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception {

        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);

        지라_이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        Map<String,Object> 반환할_지라_이슈_데이터_전송_객체
                = 지라_이슈_전략_등록_및_실행.이슈_삭제_라벨_처리하기(연결_아이디, 이슈_키_또는_아이디);

        return 반환할_지라_이슈_데이터_전송_객체;

    }

    public Map<String,Object> 이슈_연결_링크_및_서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        JiraInfoDTO 연결정보 = jiraInfo.checkInfo(연결_아이디);

        지라_이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        Map<String,Object> 반환할_지라_이슈_데이터_전송_객체
                = 지라_이슈_전략_등록_및_실행.이슈_연결_링크_및_서브테스크_가져오기(연결_아이디, 이슈_키_또는_아이디);

        return 반환할_지라_이슈_데이터_전송_객체;

    }
}
