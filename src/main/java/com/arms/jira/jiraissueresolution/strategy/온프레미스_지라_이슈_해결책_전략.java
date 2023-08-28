package com.arms.jira.jiraissueresolution.strategy;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.jiraissueresolution.model.지라_이슈_해결책_데이터_전송_객체;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Component
public class 온프레미스_지라_이슈_해결책_전략 implements 지라_이슈_해결책_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @Override
    public List<지라_이슈_해결책_데이터_전송_객체> 이슈_해결책_목록_가져오기(Long 연결_아이디) throws URISyntaxException, IOException {

        로그.info("온프레미스 지라 이슈_해결책_목록_가져오기");

        JiraInfoDTO 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(연결정보.getUri(),
                                                                        연결정보.getUserId(),
                                                                        연결정보.getPasswordOrToken());

        Iterable<Resolution> 온프레미스_이슈_해결책_목록 = restClient.getMetadataClient().getResolutions().claim();
        List<지라_이슈_해결책_데이터_전송_객체> 반환할_이슈_해결책_목록 = new ArrayList<>();

        for (Resolution 온프레미스_이슈_해결책 : 온프레미스_이슈_해결책_목록) {
            로그.info("id: " + String.valueOf(온프레미스_이슈_해결책.getId()));
            로그.info("name:" + 온프레미스_이슈_해결책.getName());
            로그.info("desc:" + 온프레미스_이슈_해결책.getDescription());

            지라_이슈_해결책_데이터_전송_객체 반환할_이슈_해결책 = new 지라_이슈_해결책_데이터_전송_객체();

            반환할_이슈_해결책.setSelf(온프레미스_이슈_해결책.getSelf().toString());
            반환할_이슈_해결책.setId(온프레미스_이슈_해결책.getId().toString());
            반환할_이슈_해결책.setName(온프레미스_이슈_해결책.getName());
            반환할_이슈_해결책.setDescription(온프레미스_이슈_해결책.getDescription());

            반환할_이슈_해결책_목록.add(반환할_이슈_해결책);
        }

        return 반환할_이슈_해결책_목록;
    }
}
