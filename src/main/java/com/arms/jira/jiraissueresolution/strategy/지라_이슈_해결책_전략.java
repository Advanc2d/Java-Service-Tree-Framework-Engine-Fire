package com.arms.jira.jiraissueresolution.strategy;

import com.arms.jira.jiraissueresolution.model.지라_이슈_해결책_데이터_전송_객체;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface 지라_이슈_해결책_전략 {

    List<지라_이슈_해결책_데이터_전송_객체> 이슈_해결책_전체_목록_가져오기(Long 연결_아이디) throws URISyntaxException, IOException;

}
