package com.arms.jira.jiraissuetype.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.jiraissuetype.model.지라_이슈_유형_데이터_전송_객체;
import com.arms.jira.jiraissuetype.strategy.온프라미스_지라_이슈_유형_전략;
import com.arms.jira.jiraissuetype.strategy.지라_이슈_유형_전략_등록_및_실행;
import com.arms.jira.jiraissuetype.strategy.클라우드_지라_이슈_유형_전략;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class 지라_이슈_유형_전략_교체 {
    지라_이슈_유형_전략_등록_및_실행 지라_이슈_유형_전략_등록_및_실행;

    클라우드_지라_이슈_유형_전략 클라우드_지라_유형_전략;

    온프라미스_지라_이슈_유형_전략 온프라미스_지라_이슈_유형_전략;

    JiraInfo jiraInfo;

    @Autowired
    public 지라_이슈_유형_전략_교체(지라_이슈_유형_전략_등록_및_실행 지라_이슈_유형_전략_등록_및_실행,
                          클라우드_지라_이슈_유형_전략 클라우드_지라_유형_전략,
                          온프라미스_지라_이슈_유형_전략 온프라미스_지라_이슈_유형_전략,
                          JiraInfo jiraInfo) {

        this.지라_이슈_유형_전략_등록_및_실행 = 지라_이슈_유형_전략_등록_및_실행;
        this.클라우드_지라_유형_전략 = 클라우드_지라_유형_전략;
        this.온프라미스_지라_이슈_유형_전략 = 온프라미스_지라_이슈_유형_전략;
        this.jiraInfo = jiraInfo;
    }

    private 지라_이슈_유형_전략_등록_및_실행 지라_프로젝트_전략_확인(JiraInfoDTO 연결정보) {

        if (연결정보.getType().equals("클라우드")) {
            지라_이슈_유형_전략_등록_및_실행.지라_이슈_유형_전략_등록(클라우드_지라_유형_전략);
        }
        else if (연결정보.getType().equals("온프라미스")) {
            지라_이슈_유형_전략_등록_및_실행.지라_이슈_유형_전략_등록(온프라미스_지라_이슈_유형_전략);
        }

        return 지라_이슈_유형_전략_등록_및_실행;
    }

    public List<지라_이슈_유형_데이터_전송_객체> 이슈_유형_전체_목록_가져오기(Long 연결_아이디) throws Exception {

        JiraInfoDTO 연결정보 = jiraInfo.loadConnectInfo(연결_아이디);

        지라_이슈_유형_전략_등록_및_실행 = 지라_프로젝트_전략_확인(연결정보);

        List<지라_이슈_유형_데이터_전송_객체> 반환할_지라_이슈_유형_목록
                = 지라_이슈_유형_전략_등록_및_실행.이슈_유형_전체_목록_가져오기(연결_아이디);

        return 반환할_지라_이슈_유형_목록;

    }
}
