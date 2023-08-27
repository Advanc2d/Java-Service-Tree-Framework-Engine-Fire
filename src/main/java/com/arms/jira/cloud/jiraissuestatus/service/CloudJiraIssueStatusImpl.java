package com.arms.jira.cloud.jiraissuestatus.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraissuestatus.model.StatusSearchDTO;
import com.arms.jira.cloud.jiraissuestatus.model.Status;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service("cloudJiraIssueStatus")
public class CloudJiraIssueStatusImpl implements CloudJiraIssueStatus {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public JiraInfo jiraInfo;

    @Override
    public StatusSearchDTO getStatusList(Long connectId) {

        logger.info("getStatusList 비즈니스 로직 실행");

        JiraInfoDTO found = jiraInfo.checkInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());
    
        int maxResult = 50;
        int startAt = 0;
        boolean checkLast = false;
    
        List<Status> values = new ArrayList<Status>();
        StatusSearchDTO result = null;
    
        while(!checkLast) {
            String endpoint = "/rest/api/3/statuses/search?maxResults="+ maxResult + "&startAt=" + startAt;
            StatusSearchDTO statuses = CloudJiraUtils.get(webClient, endpoint, StatusSearchDTO.class).block();
    
            values.addAll(statuses.getValues());
    
            if (statuses.getTotal() == values.size()) {
                result = statuses;
                result.setValues(null);
    
                checkLast = true;
            }
            else {
                startAt += maxResult;
            }
        }
    
        result.setValues(values);
    
        return result;
    }
}
