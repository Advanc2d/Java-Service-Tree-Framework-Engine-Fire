package com.arms.jira.jiraissuestatus.strategy;

import com.arms.jira.jiraissuestatus.model.지라_이슈_상태_데이터_전송_객체;

import java.util.List;

public interface 지라_이슈_상태_전략 {

    List<지라_이슈_상태_데이터_전송_객체> 이슈_상태_목록_가져오기(Long 연결_아이디) throws Exception;

    List<지라_이슈_상태_데이터_전송_객체> 프로젝트별_이슈_상태_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws Exception;

}
