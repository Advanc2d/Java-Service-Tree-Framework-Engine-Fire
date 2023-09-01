package com.arms.jira.jiraissue.service;

import com.arms.errors.codes.에러코드;
import com.arms.jira.info.model.지라연결정보_데이터;
import com.arms.jira.info.model.지라유형_정보;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.jiraissue.model.지라이슈생성_데이터;
import com.arms.jira.jiraissue.model.지라이슈_데이터;
import com.arms.jira.jiraissue.strategy.온프레미스_지라이슈_전략;
import com.arms.jira.jiraissue.strategy.지라이슈_전략_등록_및_실행;
import com.arms.jira.jiraissue.strategy.클라우드_지라이슈_전략;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class 지라이슈_전략_호출 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());


    지라이슈_전략_등록_및_실행 지라이슈_전략_등록_및_실행;

    클라우드_지라이슈_전략 클라우드_지라이슈_전략;

    온프레미스_지라이슈_전략 온프레미스_지라이슈_전략;

    지라연결_서비스 지라연결_서비스;

    @Autowired
    public 지라이슈_전략_호출(지라이슈_전략_등록_및_실행 지라이슈_전략_등록_및_실행,
                      클라우드_지라이슈_전략 클라우드_지라이슈_전략,
                      온프레미스_지라이슈_전략 온프레미스_지라이슈_전략,
                      지라연결_서비스 지라연결_서비스) {

        this.지라이슈_전략_등록_및_실행 = 지라이슈_전략_등록_및_실행;
        this.클라우드_지라이슈_전략 = 클라우드_지라이슈_전략;
        this.온프레미스_지라이슈_전략 = 온프레미스_지라이슈_전략;
        this.지라연결_서비스 = 지라연결_서비스;
    }

    private 지라이슈_전략_등록_및_실행 지라_이슈_전략_확인(지라연결정보_데이터 연결정보) {

        if (연결정보 == null || 연결정보.getType().isEmpty()) {
            로그.error("지라이슈 전략 등록 Error: 연결정보_유형 " + 에러코드.서버_유형_정보없음.getErrorMsg());
            throw new IllegalArgumentException("지라이슈 전략 등록 Error: 연결정보_유형 " + 에러코드.서버_유형_정보없음.getErrorMsg());
        }

        지라유형_정보 지라_유형 = 지라유형_정보.valueOf(연결정보.getType());

        if (지라_유형 == 지라유형_정보.클라우드) {
            지라이슈_전략_등록_및_실행.지라_이슈_전략_등록(클라우드_지라이슈_전략);
        }
        else if (지라_유형 == 지라유형_정보.온프레미스) {
            지라이슈_전략_등록_및_실행.지라_이슈_전략_등록(온프레미스_지라이슈_전략);
        }

        return 지라이슈_전략_등록_및_실행;
    }

    public List<지라이슈_데이터> 이슈_전체_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) throws Exception{

        if (연결_아이디 == null) {
            로그.error("이슈 전체 목록 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 전체 목록 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
        }

        if (프로젝트_키_또는_아이디 == null || 프로젝트_키_또는_아이디.isEmpty()) {
            로그.error("이슈 전체 목록 가져오기 Error 프로젝트_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 전체 목록 가져오기 Error 프로젝트_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
        }

        지라연결정보_데이터 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        List<지라이슈_데이터> 반환할_지라이슈_데이터
                = 지라이슈_전략_등록_및_실행.이슈_전체_목록_가져오기(연결_아이디, 프로젝트_키_또는_아이디);

        return 반환할_지라이슈_데이터;

    }

    public 지라이슈_데이터 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception{

        if (연결_아이디 == null) {
            로그.error("이슈 상세정보 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 상세정보 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 상세정보 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 상세정보 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
        }

        지라연결정보_데이터 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        지라이슈_데이터 반환할_지라이슈_데이터
                = 지라이슈_전략_등록_및_실행.이슈_상세정보_가져오기(연결_아이디, 이슈_키_또는_아이디);

        return 반환할_지라이슈_데이터;

    }

    public 지라이슈_데이터 이슈_생성하기(Long 연결_아이디,
                            지라이슈생성_데이터 지라이슈생성_데이터) throws Exception {

        if (연결_아이디 == null) {
            로그.error("이슈 생성하기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 생성하기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
        }

        if (지라이슈생성_데이터 == null) {
            로그.error("이슈 생성하기 Error 지라이슈생성_데이터가 없습니다.");
            throw new IllegalArgumentException("이슈 생성하기 Error 지라이슈생성_데이터가 없습니다.");
        }

        지라연결정보_데이터 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        지라이슈_데이터 반환할_지라이슈_데이터
                = 지라이슈_전략_등록_및_실행.이슈_생성하기(연결_아이디, 지라이슈생성_데이터);

        return 반환할_지라이슈_데이터;

    }

    public Map<String,Object> 이슈_수정하기(Long 연결_아이디, String 이슈키,
                                      지라이슈생성_데이터 지라이슈생성_데이터) throws Exception {

        if (연결_아이디 == null) {
            로그.error("이슈 수정하기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 수정하기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
        }

        if (지라이슈생성_데이터 == null) {
            로그.error("이슈 수정하기 Error 지라이슈생성_데이터가 없습니다.");
            throw new IllegalArgumentException("이슈 수정하기 Error 지라이슈생성_데이터가 없습니다.");
        }

        지라연결정보_데이터 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        Map<String,Object> 이슈수정_결과
                = 지라이슈_전략_등록_및_실행.이슈_수정하기(연결_아이디, 이슈키, 지라이슈생성_데이터);

        return 이슈수정_결과;

    }

    public Map<String,Object> 이슈_삭제_라벨_처리하기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception {

        if (연결_아이디 == null) {
            로그.error("이슈 삭제 라벨 처리하기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 삭제 라벨 처리하기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 삭제 라벨 처리하기 Error 이슈_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 삭제 라벨 처리하기 Error 이슈_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
        }

        지라연결정보_데이터 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        Map<String,Object> 이슈삭제라벨_결과
                = 지라이슈_전략_등록_및_실행.이슈_삭제_라벨_처리하기(연결_아이디, 이슈_키_또는_아이디);

        return 이슈삭제라벨_결과;

    }

    public List<지라이슈_데이터> 이슈링크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception{

        if (연결_아이디 == null) {
            로그.error("이슈 링크 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 링크 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 링크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 링크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
        }

        지라연결정보_데이터 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        List<지라이슈_데이터> 반환할_이슈링크_목록
                = 지라이슈_전략_등록_및_실행.이슈링크_가져오기(연결_아이디, 이슈_키_또는_아이디);

        return 반환할_이슈링크_목록;

    }

    public List<지라이슈_데이터> 서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception{

        if (연결_아이디 == null) {
            로그.error("이슈 서브테스크 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 서브테스크 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 서브테스크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 서브테스크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
        }

        지라연결정보_데이터 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라이슈_전략_등록_및_실행 = 지라_이슈_전략_확인(연결정보);

        List<지라이슈_데이터> 반환할_이슈링크_목록
                = 지라이슈_전략_등록_및_실행.서브테스크_가져오기(연결_아이디, 이슈_키_또는_아이디);

        return 반환할_이슈링크_목록;
    }
}
