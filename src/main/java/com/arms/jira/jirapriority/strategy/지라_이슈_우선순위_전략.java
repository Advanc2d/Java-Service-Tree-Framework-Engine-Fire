package com.arms.jira.jirapriority.strategy;

import com.arms.jira.jirapriority.model.지라_이슈_우선순위_데이터_전송_객체;

import java.util.List;

public interface 지라_이슈_우선순위_전략 {
    List<지라_이슈_우선순위_데이터_전송_객체> 우선순위_전체_목록_가져오기(Long 연결_아이디) throws Exception;
}
