package com.arms.jira.cloud.jiraissuestatus.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.jira.utils.지라유틸;
import com.arms.jira.cloud.jiraissuestatus.model.StatusSearchDTO;
import com.arms.jira.cloud.jiraissuestatus.model.Status;
import com.arms.jira.info.model.지라연결정보_데이터;
import com.arms.jira.info.service.지라연결_서비스;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service("cloudJiraIssueStatus")
public class CloudJiraIssueStatusImpl implements CloudJiraIssueStatus {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public 지라연결_서비스 지라연결_서비스;

    @Autowired
    private 지라유틸 지라유틸;

    @Override
    public StatusSearchDTO getStatusList(Long connectId) {

        logger.info("getStatusList 비즈니스 로직 실행");

        지라연결정보_데이터 found = 지라연결_서비스.checkInfo(connectId);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(found.getUri(), found.getUserId(), found.getPasswordOrToken());
    
        int startAt = 0;
        int 최대_검색수 = 지라유틸.최대_검색수_가져오기();
        boolean checkLast = false;
    
        List<Status> values = new ArrayList<Status>();
        StatusSearchDTO result = null;
    
        while(!checkLast) {
            String endpoint = "/rest/api/3/statuses/search?maxResults="+ 최대_검색수 + "&startAt=" + startAt;
            StatusSearchDTO statuses = 지라유틸.get(webClient, endpoint, StatusSearchDTO.class).block();
    
            values.addAll(statuses.getValues());
    
            if (statuses.getTotal() == values.size()) {
                result = statuses;
                result.setValues(null);
    
                checkLast = true;
            }
            else {
                startAt += 최대_검색수;
            }
        }
    
        result.setValues(values);
    
        return result;
    }
}
