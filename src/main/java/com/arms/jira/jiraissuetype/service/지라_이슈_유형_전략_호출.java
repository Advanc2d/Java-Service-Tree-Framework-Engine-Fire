package com.arms.jira.jiraissuetype.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.model.지라_유형_정보;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.jiraissuetype.model.지라_이슈_유형_데이터_전송_객체;
import com.arms.jira.jiraissuetype.strategy.온프레미스_지라_이슈_유형_전략;
import com.arms.jira.jiraissuetype.strategy.지라_이슈_유형_전략_등록_및_실행;
import com.arms.jira.jiraissuetype.strategy.클라우드_지라_이슈_유형_전략;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class 지라_이슈_유형_전략_호출 {
    지라_이슈_유형_전략_등록_및_실행 지라_이슈_유형_전략_등록_및_실행;

    클라우드_지라_이슈_유형_전략 클라우드_지라_유형_전략;

    온프레미스_지라_이슈_유형_전략 온프레미스_지라_이슈_유형_전략;

    지라연결_서비스 지라연결_서비스;

    @Autowired
    public 지라_이슈_유형_전략_호출(지라_이슈_유형_전략_등록_및_실행 지라_이슈_유형_전략_등록_및_실행,
                          클라우드_지라_이슈_유형_전략 클라우드_지라_유형_전략,
                          온프레미스_지라_이슈_유형_전략 온프레미스_지라_이슈_유형_전략,
                          지라연결_서비스 지라연결_서비스) {

        this.지라_이슈_유형_전략_등록_및_실행 = 지라_이슈_유형_전략_등록_및_실행;
        this.클라우드_지라_유형_전략 = 클라우드_지라_유형_전략;
        this.온프레미스_지라_이슈_유형_전략 = 온프레미스_지라_이슈_유형_전략;
        this.지라연결_서비스 = 지라연결_서비스;
    }

    private 지라_이슈_유형_전략_등록_및_실행 지라_이슈_유형_전략_확인(JiraInfoDTO 연결정보) {


        if(연결정보 == null || 연결정보.getType().isEmpty()) {
            throw new IllegalArgumentException("연결정보가 없습니다.");
        }

        지라_유형_정보 지라_유형 = 지라_유형_정보.valueOf(연결정보.getType());

        if (지라_유형 == 지라_유형_정보.클라우드) {
            지라_이슈_유형_전략_등록_및_실행.지라_이슈_유형_전략_등록(클라우드_지라_유형_전략);
        }
        else if (지라_유형 == 지라_유형_정보.온프레미스) {
            지라_이슈_유형_전략_등록_및_실행.지라_이슈_유형_전략_등록(온프레미스_지라_이슈_유형_전략);
        }

        return 지라_이슈_유형_전략_등록_및_실행;

    }

    public List<지라_이슈_유형_데이터_전송_객체> 이슈_유형_목록_가져오기(Long 연결_아이디) throws Exception {

        JiraInfoDTO 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라_유형_정보 지라_유형 = 지라_유형_정보.valueOf(연결정보.getType());

        if (지라_유형 == 지라_유형_정보.클라우드) {
            /* ***
             *  에러처리 수정사항, 클라우드 타입은 이슈 유형 목록 가져오기 안 됨
             *** */
            throw new IllegalArgumentException("클라우드 타입은 이슈 유형 목록 가져오기를 사용할 수 없습니다.");
        }

        지라_이슈_유형_전략_등록_및_실행 = 지라_이슈_유형_전략_확인(연결정보);

        List<지라_이슈_유형_데이터_전송_객체> 반환할_지라_이슈_유형_목록
                = 지라_이슈_유형_전략_등록_및_실행.이슈_유형_목록_가져오기(연결_아이디);

        return 반환할_지라_이슈_유형_목록;

    }

    public List<지라_이슈_유형_데이터_전송_객체> 프로젝트별_이슈_유형_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws Exception {

        JiraInfoDTO 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라_유형_정보 지라_유형 = 지라_유형_정보.valueOf(연결정보.getType());

        if (지라_유형 == 지라_유형_정보.온프레미스) {
            /* ***
            *  에러처리 수정사항, 온프레미스 타입은 프로젝트별 이슈 유형 목록 가져오기 안 됨
            *** */
            throw new IllegalArgumentException("온프레미스 타입은 프로젝트별 이슈 유형 목록 가져오기를 사용할 수 없습니다.");
        }

        지라_이슈_유형_전략_등록_및_실행 = 지라_이슈_유형_전략_확인(연결정보);

        List<지라_이슈_유형_데이터_전송_객체> 반환할_지라_이슈_유형_목록
                = 지라_이슈_유형_전략_등록_및_실행.프로젝트별_이슈_유형_목록_가져오기(연결_아이디, 프로젝트_아이디);

        return 반환할_지라_이슈_유형_목록;

    }
}
