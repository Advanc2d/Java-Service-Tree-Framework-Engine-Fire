package com.arms.jira.onpremise.jiraissueresolution.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.onpremise.jiraissueresolution.model.OnPremiseJiraIssueResolutionDTO;
import com.arms.jira.utils.지라유틸;
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
public class OnPremiseJiraIssueResolutionImpl implements OnPremiseJiraIssueResolution {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @Override
    public List<OnPremiseJiraIssueResolutionDTO> getResolutionList(Long connectId) throws Exception {
        JiraInfoDTO info = 지라연결_서비스.checkInfo(connectId);
        JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(info.getUri(),
                                                                         info.getUserId(),
                                                                         info.getPasswordOrToken());

        Iterable<Resolution> allResolution = restClient.getMetadataClient().getResolutions().claim();
        List<OnPremiseJiraIssueResolutionDTO> resolutionList = new ArrayList<>();

        for (Resolution resolution : allResolution) {
            logger.info("id: " + String.valueOf(resolution.getId()));
            logger.info("name:" + resolution.getName());
            logger.info("desc:" + resolution.getDescription());

            OnPremiseJiraIssueResolutionDTO jiraResolution = new OnPremiseJiraIssueResolutionDTO();
            jiraResolution.setSelf(resolution.getSelf().toString());
            jiraResolution.setId(resolution.getId().toString());
            jiraResolution.setName(resolution.getName());
            jiraResolution.setDescription(resolution.getDescription());

            resolutionList.add(jiraResolution);
        }

        return resolutionList;
    }

}
