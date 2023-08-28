package com.arms.jira.jirapriority.strategy;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.jirapriority.model.지라_이슈_우선순위_데이터_전송_객체;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class 온프레미스_지라_이슈_우선순위_전략 implements 지라_이슈_우선순위_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @Override
    public List<지라_이슈_우선순위_데이터_전송_객체> 우선순위_전체_목록_가져오기(Long 연결_아이디) throws Exception {

        로그.info("온프레미스 지라 이슈 우선순위 전체 목록 가져오기");

        JiraInfoDTO 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(연결정보.getUri(),
                                                                         연결정보.getUserId(),
                                                                         연결정보.getPasswordOrToken());

        Iterable<Priority> 모든_지라_이슈_우선순위 = restClient.getMetadataClient().getPriorities().claim();
        List<지라_이슈_우선순위_데이터_전송_객체> 반환할_지라_이슈_우선순위_데이터전송객체_목록 = new ArrayList<>();

        for (Priority priority : 모든_지라_이슈_우선순위) {

            지라_이슈_우선순위_데이터_전송_객체 온프레미스_지라_이슈_우선순위데이터전송객체 = new 지라_이슈_우선순위_데이터_전송_객체();
            온프레미스_지라_이슈_우선순위데이터전송객체.setSelf(priority.getSelf().toString());
            온프레미스_지라_이슈_우선순위데이터전송객체.setId(priority.getId().toString());
            온프레미스_지라_이슈_우선순위데이터전송객체.setName(priority.getName());
            온프레미스_지라_이슈_우선순위데이터전송객체.setDescription(priority.getDescription());

            반환할_지라_이슈_우선순위_데이터전송객체_목록.add(온프레미스_지라_이슈_우선순위데이터전송객체);
        }

        return 반환할_지라_이슈_우선순위_데이터전송객체_목록;
    }

}
