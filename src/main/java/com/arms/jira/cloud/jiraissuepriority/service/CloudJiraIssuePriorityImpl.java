package com.arms.jira.cloud.jiraissuepriority.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraissuepriority.model.Priority;
import com.arms.jira.cloud.jiraissuepriority.model.PrioritySearchDTO;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.지라연결_서비스;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service("cloudJiraIssuePriority")
public class CloudJiraIssuePriorityImpl implements CloudJiraIssuePriority {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public 지라연결_서비스 지라연결_서비스;

    @Override
    public PrioritySearchDTO getPriorityList(Long connectId) {

        JiraInfoDTO found = 지라연결_서비스.checkInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        int maxResult = 50;
        int startAt = 0;
        int index= 1;
        boolean checkLast = false;

        List<Priority> values = new ArrayList<Priority>();
        PrioritySearchDTO result = null;

        while(!checkLast) {
            String endpoint = "/rest/api/3/priority/search?maxResults="+ maxResult + "&startAt=" + startAt;
            PrioritySearchDTO priorities = CloudJiraUtils.get(webClient, endpoint, PrioritySearchDTO.class).block();

            values.addAll(priorities.getValues());

            if (priorities.getTotal() == values.size()) {
                result = priorities;
                result.setValues(null);

                checkLast = true;
            }
            else {
                startAt = maxResult * index;
                index++;
            }
        }

        result.setValues(values);

        return result;
    }
}
