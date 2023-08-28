package com.arms.jira.cloud.jiraissueresolution.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraissueresolution.model.Resolution;
import com.arms.jira.cloud.jiraissueresolution.model.ResolutionSearchDTO;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.지라연결_서비스;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service("cloudJiraIssueResolution")
public class CloudJiraIssueResolutionImpl implements CloudJiraIssueResolution {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public 지라연결_서비스 지라연결_서비스;

    @Override
    public ResolutionSearchDTO getResolutionList(Long connectId) {

        JiraInfoDTO found = 지라연결_서비스.checkInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        int maxResult = 50;
        int startAt = 0;
        int index= 1;
        boolean checkLast = false;

        List<Resolution> values = new ArrayList<Resolution>();
        ResolutionSearchDTO result = null;

        while(!checkLast) {
            String endpoint = "/rest/api/3/resolution/search?maxResults="+ maxResult + "&startAt=" + startAt;
            ResolutionSearchDTO resolutions = CloudJiraUtils.get(webClient, endpoint, ResolutionSearchDTO.class).block();

            values.addAll(resolutions.getValues());

            if (resolutions.getTotal() == values.size()) {
                result = resolutions;
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
