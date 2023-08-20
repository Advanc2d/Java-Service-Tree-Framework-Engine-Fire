package com.arms.jira.jiraissuetype.strategy;

import com.arms.jira.jiraissuetype.model.지라_이슈_유형_데이터_전송_객체;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class 지라_이슈_유형_전략_등록_및_실행 {

    지라_이슈_유형_전략 지라_이슈_유형_전략;

    public List<지라_이슈_유형_데이터_전송_객체> 이슈_유형_전체_목록_가져오기(Long 연결_아이디) throws Exception {
        return this.지라_이슈_유형_전략.이슈_유형_전체_목록_가져오기(연결_아이디);
    }

    public void 지라_이슈_유형_전략_등록(지라_이슈_유형_전략 지라_이슈_유형_전략) {
        this.지라_이슈_유형_전략 = 지라_이슈_유형_전략;
    }

}
