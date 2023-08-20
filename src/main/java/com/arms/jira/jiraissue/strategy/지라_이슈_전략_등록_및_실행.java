package com.arms.jira.jiraissue.strategy;

import com.arms.jira.jiraissue.model.지라_이슈_데이터_전송_객체;
import com.arms.jira.jiraissue.model.지라_이슈_생성_데이터_전송_객체;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class 지라_이슈_전략_등록_및_실행 {

    지라_이슈_전략 지라_이슈_전략;

    public List<지라_이슈_데이터_전송_객체> 이슈_전체_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {
        return this.지라_이슈_전략.이슈_전체_목록_가져오기(연결_아이디, 프로젝트_키_또는_아이디);
    }

    public 지라_이슈_데이터_전송_객체 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {
        return this.지라_이슈_전략.이슈_상세정보_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    public 지라_이슈_데이터_전송_객체 이슈_생성하기(Long 연결_아이디,
                            지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) {
        return this.지라_이슈_전략.이슈_생성하기(연결_아이디, 지라_이슈_생성_데이터_전송_객체);
    }

    public Map<String,Object> 이슈_수정하기(Long 연결_아이디, String 이슈_키_또는_아이디,
                               지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) {
        return this.지라_이슈_전략.이슈_수정하기(연결_아이디, 이슈_키_또는_아이디, 지라_이슈_생성_데이터_전송_객체);
    }

    public Map<String,Object> 이슈_삭제하기(Long 연결_아이디, String 이슈_키_또는_아이디) {
        return this.지라_이슈_전략.이슈_삭제하기(연결_아이디, 이슈_키_또는_아이디);
    }

    public Map<String,Object> 이슈_연결_링크_및_서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {
        return this.지라_이슈_전략.이슈_연결_링크_및_서브테스크_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    public void 지라_이슈_전략_등록(지라_이슈_전략 지라_이슈_전략) {
        this.지라_이슈_전략 = 지라_이슈_전략;
    }

}
