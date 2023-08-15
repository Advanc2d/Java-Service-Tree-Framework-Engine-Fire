package com.arms.jira.onpremise.jiraresolution.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.arms.jira.onpremise.jiraresolution.model.OnPremiseJiraResolutionDTO;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service("onPremiseJiraResolution")
public class OnPremiseJiraResolutionImpl implements OnPremiseJiraResolution {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Override
    public List<OnPremiseJiraResolutionDTO> getResolutionList(Long connectId) throws Exception {
        JiraInfoDTO info = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                                                                         info.getUserId(),
                                                                         info.getPasswordOrToken());

        Iterable<Resolution> allResolution = restClient.getMetadataClient().getResolutions().claim();
        List<OnPremiseJiraResolutionDTO> resolutionList = new ArrayList<>();

        for (Resolution resolution : allResolution) {
            logger.info("id: " + String.valueOf(resolution.getId()));
            logger.info("name:" + resolution.getName());
            logger.info("desc:" + resolution.getDescription());

            OnPremiseJiraResolutionDTO jiraResolution = new OnPremiseJiraResolutionDTO();
            jiraResolution.setSelf(resolution.getSelf().toString());
            jiraResolution.setId(resolution.getId().toString());
            jiraResolution.setName(resolution.getName());
            jiraResolution.setDescription(resolution.getDescription());

            resolutionList.add(jiraResolution);
        }

        return resolutionList;
    }

}
