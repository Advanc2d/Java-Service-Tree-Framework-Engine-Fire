package com.arms.jira.jiraproject.strategy;

import com.arms.jira.jiraproject.model.지라_프로젝트_데이터_전송_객체;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface 지라_프로젝트_전략 {

    지라_프로젝트_데이터_전송_객체 프로젝트_상세정보_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) throws Exception;

    List<지라_프로젝트_데이터_전송_객체> 프로젝트_전체_목록_가져오기(Long 연결_아이디) throws URISyntaxException, IOException;

}
