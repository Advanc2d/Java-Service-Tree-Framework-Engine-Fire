package com.arms.jira.jiraissue.strategy;

import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.jiraissue.model.지라_이슈_데이터_전송_객체;
import com.arms.jira.jiraissue.model.지라_이슈_생성_데이터_전송_객체;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class 클라우드_지라_이슈_전략 implements 지라_이슈_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Override
    public List<지라_이슈_데이터_전송_객체> 이슈_전체_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {
        return null;
    }

    @Override
    public 지라_이슈_데이터_전송_객체 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {
        return null;
    }

    @Override
    public 지라_이슈_데이터_전송_객체 이슈_생성하기(Long 연결_아이디, 지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) {
        return null;
    }

    @Override
    public Map<String, Object> 이슈_수정하기(Long 연결_아이디, String 이슈_키_또는_아이디, 지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) {
        return null;
    }

    @Override
    public Map<String, Object> 이슈_삭제하기(Long 연결_아이디, String 이슈_키_또는_아이디) {
        return null;
    }

    @Override
    public Map<String, Object> 이슈_연결_링크_및_서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {
        return null;
    }
}
