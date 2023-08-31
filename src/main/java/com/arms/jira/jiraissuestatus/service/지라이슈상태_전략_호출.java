package com.arms.jira.jiraissuestatus.service;

import com.arms.errors.codes.에러코드;
import com.arms.jira.info.model.지라연결정보_데이터;
import com.arms.jira.info.model.지라유형_정보;
import com.arms.jira.info.service.지라연결_서비스;

import com.arms.jira.jiraissuestatus.model.지라이슈상태_데이터;
import com.arms.jira.jiraissuestatus.strategy.클라우드_지라이슈상태_전략;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arms.jira.jiraissuestatus.strategy.지라이슈상태_전략_등록_및_실행;
import com.arms.jira.jiraissuestatus.strategy.온프레미스_지라이슈상태_전략;


import java.util.List;

@Service
@RequiredArgsConstructor
public class 지라이슈상태_전략_호출 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    지라이슈상태_전략_등록_및_실행 지라이슈상태_전략_등록_및_실행;

    클라우드_지라이슈상태_전략 클라우드_지라_이슈_상태_전략;

    온프레미스_지라이슈상태_전략 온프레미스_지라이슈상태_전략;

    지라연결_서비스 지라연결_서비스;

    @Autowired
    public 지라이슈상태_전략_호출(지라이슈상태_전략_등록_및_실행 지라이슈상태_전략_등록_및_실행,
                        클라우드_지라이슈상태_전략 클라우드_지라_이슈_상태_전략,
                        온프레미스_지라이슈상태_전략 온프레미스_지라이슈상태_전략,
                        지라연결_서비스 지라연결_서비스) {

        this.지라이슈상태_전략_등록_및_실행 = 지라이슈상태_전략_등록_및_실행;
        this.클라우드_지라_이슈_상태_전략 = 클라우드_지라_이슈_상태_전략;
        this.온프레미스_지라이슈상태_전략 = 온프레미스_지라이슈상태_전략;
        this.지라연결_서비스 = 지라연결_서비스;
    }

    private 지라이슈상태_전략_등록_및_실행 지라_이슈_상태_전략_확인(지라연결정보_데이터 연결정보) throws Exception {

        if (연결정보 == null || 연결정보.getType().isEmpty()) {
            로그.error("지라이슈 상태 전략 등록 Error: 연결정보_유형 " + 에러코드.서버_유형_정보없음.getErrorMsg());
            throw new IllegalArgumentException("지라이슈 상태 전략 등록 Error: 연결정보_유형 " + 에러코드.서버_유형_정보없음.getErrorMsg());
        }

        지라유형_정보 지라_유형 = 지라유형_정보.valueOf(연결정보.getType());

        if (지라_유형 == 지라유형_정보.클라우드) {
            지라이슈상태_전략_등록_및_실행.지라_이슈_상태_전략_등록(클라우드_지라_이슈_상태_전략);
        }
        else if (지라_유형 == 지라유형_정보.온프레미스) {
            지라이슈상태_전략_등록_및_실행.지라_이슈_상태_전략_등록(온프레미스_지라이슈상태_전략);
        }

        return 지라이슈상태_전략_등록_및_실행;

    }

    public List<지라이슈상태_데이터> 이슈_상태_목록_가져오기(Long 연결_아이디) throws Exception {

        if (연결_아이디 == null) {
            로그.error("이슈 상태 목록 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 상태 목록 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
        }

        지라연결정보_데이터 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라유형_정보 지라_유형 = 지라유형_정보.valueOf(연결정보.getType());

        if (지라_유형 == 지라유형_정보.클라우드) {
            /* ***
             *  에러처리 수정사항, 클라우드 타입은 이슈 유형 목록 가져오기 안 됨
             *** */
            throw new IllegalArgumentException("클라우드 타입은 이슈 상태 목록 가져오기를 사용할 수 없습니다.");
        }

        지라이슈상태_전략_등록_및_실행 = 지라_이슈_상태_전략_확인(연결정보);

        List<지라이슈상태_데이터> 반환할_지라_이슈_상태_데이터전송객체_목록
                = 지라이슈상태_전략_등록_및_실행.이슈_상태_목록_가져오기(연결_아이디);

        return 반환할_지라_이슈_상태_데이터전송객체_목록;

    }


    public List<지라이슈상태_데이터> 프로젝트별_이슈_상태_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws Exception {

        if (연결_아이디 == null) {
            로그.error("프로젝트별_이슈_상태_목록_가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("프로젝트별_이슈_상태_목록_가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
        }

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            로그.error("프로젝트별_이슈_상태_목록_가져오기 Error 프로젝트_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
            throw new IllegalArgumentException("프로젝트별_이슈_상태_목록_가져오기 Error 프로젝트_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
        }

        지라연결정보_데이터 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라유형_정보 지라_유형 = 지라유형_정보.valueOf(연결정보.getType());

        if (지라_유형 == 지라유형_정보.온프레미스) {
            /* ***
             *  에러처리 수정사항, 온프레미스 타입은 프로젝트별 이슈 유형 목록 가져오기 안 됨
             *** */
            throw new IllegalArgumentException("온프레미스 타입은 프로젝트별 이슈 상태 목록 가져오기를 사용할 수 없습니다.");
        }

        지라이슈상태_전략_등록_및_실행 = 지라_이슈_상태_전략_확인(연결정보);

        List<지라이슈상태_데이터> 반환할_지라_이슈_상태_데이터전송객체_목록
                = 지라이슈상태_전략_등록_및_실행.프로젝트별_이슈_상태_목록_가져오기(연결_아이디, 프로젝트_아이디);

        return 반환할_지라_이슈_상태_데이터전송객체_목록;
    }
}
