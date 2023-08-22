package com.arms.jira.jiraissuestatus.strategy;


import com.arms.jira.jiraissuestatus.model.지라_이슈_상태_데이터_전송_객체;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class 지라_이슈_상태_전략_등록_및_실행 {

    지라_이슈_상태_전략 지라_이슈_상태_전략;


    com.arms.jira.jiraissueresolution.strategy.지라_이슈_해결책_전략 지라_이슈_해결책_전략;

    public List<지라_이슈_상태_데이터_전송_객체> 이슈_상태_목록_가져오기(Long 연결_아이디) throws Exception {
        return this.지라_이슈_상태_전략.이슈_상태_목록_가져오기(연결_아이디);
    }

    public List<지라_이슈_상태_데이터_전송_객체> 프로젝트별_이슈_상태_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws Exception {
        return this.지라_이슈_상태_전략.프로젝트별_이슈_상태_목록_가져오기(연결_아이디, 프로젝트_아이디);
    }

    public void 지라_이슈_상태_전략_등록(지라_이슈_상태_전략 지라_이슈_상태_전략) throws Exception{

        this.지라_이슈_상태_전략 = 지라_이슈_상태_전략;
    }
}
