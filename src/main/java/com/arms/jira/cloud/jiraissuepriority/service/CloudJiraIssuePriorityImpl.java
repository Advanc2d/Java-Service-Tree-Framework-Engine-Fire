package com.arms.jira.cloud.jiraissuepriority.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.jira.utils.지라유틸;
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

    @Autowired
    private 지라유틸 지라유틸;

    @Override
    public PrioritySearchDTO getPriorityList(Long connectId) {

        JiraInfoDTO found = 지라연결_서비스.checkInfo(connectId);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        int 최대_검색수 = 지라유틸.최대_검색수_가져오기();
        int startAt = 0;
        int index= 1;
        boolean checkLast = false;

        List<Priority> values = new ArrayList<Priority>();
        PrioritySearchDTO result = null;

        while(!checkLast) {
            String endpoint = "/rest/api/3/priority/search?maxResults="+ 최대_검색수 + "&startAt=" + startAt;
            PrioritySearchDTO priorities = 지라유틸.get(webClient, endpoint, PrioritySearchDTO.class).block();

            values.addAll(priorities.getValues());

            if (priorities.getTotal() == values.size()) {
                result = priorities;
                result.setValues(null);

                checkLast = true;
            }
            else {
                startAt = 최대_검색수 * index;
                index++;
            }
        }

        result.setValues(values);

        return result;
    }
}
