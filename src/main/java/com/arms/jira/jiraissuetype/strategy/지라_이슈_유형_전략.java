package com.arms.jira.jiraissuetype.strategy;

import com.arms.jira.jiraissuetype.model.지라_이슈_유형_데이터_전송_객체;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface 지라_이슈_유형_전략 {

    List<지라_이슈_유형_데이터_전송_객체> 이슈_유형_전체_목록_가져오기(Long 연결_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException;

}
