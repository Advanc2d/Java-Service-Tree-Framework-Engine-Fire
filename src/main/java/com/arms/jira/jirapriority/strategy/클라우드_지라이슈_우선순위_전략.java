package com.arms.jira.jirapriority.strategy;

import com.arms.jira.utils.지라유틸;
import com.arms.jira.info.model.지라연결정보_데이터;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.jirapriority.model.지라이슈_우선순위_데이터;
import com.arms.jira.jirapriority.model.클라우드_지라이슈_우선순위_전체_데이터;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class 클라우드_지라이슈_우선순위_전략 implements 지라이슈_우선순위_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @Autowired
    private 지라유틸 지라유틸;

    @Override
    public List<지라이슈_우선순위_데이터> 우선순위_전체_목록_가져오기(Long 연결_아이디) throws Exception {

        로그.info("클라우드 지라 이슈 우선순위 전체 목록 가져오기");

        지라연결정보_데이터 found = 지라연결_서비스.checkInfo(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        int 최대_검색수 = 지라유틸.최대_검색수_가져오기();
        int startAt = 0;
        boolean isLast = false;

        List<지라이슈_우선순위_데이터> 반환할_지라_이슈_우선순위_데이터전송객체_목록 = new ArrayList<>();

        while(!isLast) {

            String endpoint = "/rest/api/3/priority/search?maxResults="+ 최대_검색수 + "&startAt=" + startAt;
            클라우드_지라이슈_우선순위_전체_데이터 클라우드_지라이슈_우선순위_전체_데이터 = 지라유틸.get(webClient, endpoint, 클라우드_지라이슈_우선순위_전체_데이터.class).block();

            반환할_지라_이슈_우선순위_데이터전송객체_목록.addAll(클라우드_지라이슈_우선순위_전체_데이터.getValues());

            if (클라우드_지라이슈_우선순위_전체_데이터.getTotal() == 반환할_지라_이슈_우선순위_데이터전송객체_목록.size()) {
                isLast = true;
            }
            else {
                startAt += 최대_검색수;
            }
        }

        return 반환할_지라_이슈_우선순위_데이터전송객체_목록;
    }
}
