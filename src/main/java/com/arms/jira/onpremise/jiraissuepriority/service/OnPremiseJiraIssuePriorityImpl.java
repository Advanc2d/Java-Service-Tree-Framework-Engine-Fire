package com.arms.jira.onpremise.jiraissuepriority.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.onpremise.jiraissuepriority.model.OnPremiseJiraIssuePriorityDTO;
import com.arms.jira.utils.지라유틸;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Priority;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service("onPremiseJiraPriority")
public class OnPremiseJiraIssuePriorityImpl implements OnPremiseJiraIssuePriority {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @Override
    public List<OnPremiseJiraIssuePriorityDTO> getPriorityList(Long connectId) throws Exception {
        JiraInfoDTO info = 지라연결_서비스.checkInfo(connectId);

        JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(info.getUri(),
                                                                         info.getUserId(),
                                                                         info.getPasswordOrToken());

        Iterable<Priority> allPriority = restClient.getMetadataClient().getPriorities().claim();
        List<OnPremiseJiraIssuePriorityDTO> priorityList = new ArrayList<>();

        for (Priority priority : allPriority) {
            logger.info("id: " + String.valueOf(priority.getId()));
            logger.info("name:" + priority.getName());
            logger.info("desc:" + priority.getDescription());

            OnPremiseJiraIssuePriorityDTO jiraPriority = new OnPremiseJiraIssuePriorityDTO();
            jiraPriority.setSelf(priority.getSelf().toString());
            jiraPriority.setId(priority.getId().toString());
            jiraPriority.setName(priority.getName());
            jiraPriority.setDescription(priority.getDescription());

            priorityList.add(jiraPriority);
        }

        return priorityList;
    }

}
