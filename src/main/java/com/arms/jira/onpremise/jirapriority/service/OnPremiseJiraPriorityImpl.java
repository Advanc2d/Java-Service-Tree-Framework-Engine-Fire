package com.arms.jira.onpremise.jirapriority.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.arms.jira.onpremise.jirapriority.model.OnPremiseJiraPriorityDTO;
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
public class OnPremiseJiraPriorityImpl implements OnPremiseJiraPriority{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Override
    public List<OnPremiseJiraPriorityDTO> getPriorityList(Long connectId) throws Exception {
        JiraInfoDTO info = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                                                                         info.getUserId(),
                                                                         info.getPasswordOrToken());

        Iterable<Priority> allPriority = restClient.getMetadataClient().getPriorities().claim();
        List<OnPremiseJiraPriorityDTO> priorityList = new ArrayList<>();

        for (Priority priority : allPriority) {
            logger.info("id: " + String.valueOf(priority.getId()));
            logger.info("name:" + priority.getName());
            logger.info("desc:" + priority.getDescription());

            OnPremiseJiraPriorityDTO jiraPriority = new OnPremiseJiraPriorityDTO();
            jiraPriority.setSelf(priority.getSelf().toString());
            jiraPriority.setId(priority.getId().toString());
            jiraPriority.setName(priority.getName());
            jiraPriority.setDescription(priority.getDescription());

            priorityList.add(jiraPriority);
        }

        return priorityList;
    }

}
