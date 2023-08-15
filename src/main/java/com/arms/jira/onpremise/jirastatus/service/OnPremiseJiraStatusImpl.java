package com.arms.jira.onpremise.jirastatus.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.onpremise.OnPremiseJiraUtils;

import com.arms.jira.onpremise.jirastatus.model.OnPremiseJiraStatusDTO;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import io.atlassian.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;


@AllArgsConstructor
@Service("onPremiseJiraStatus")
public class OnPremiseJiraStatusImpl implements OnPremiseJiraStatus{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<OnPremiseJiraStatusDTO> getStatusList(Long connectId) throws Exception {
        JiraInfoDTO info = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                info.getUserId(),
                info.getPasswordOrToken());

        // 상태 리스트 조회 로직 추가
        Promise<Iterable<Status>> statusesPromise = restClient.getMetadataClient().getStatuses();
        Iterable<Status> statuses = statusesPromise.claim();

        List<OnPremiseJiraStatusDTO> onPremiseJiraStatusDTOList = new ArrayList<>();
        for (Status status : statuses) {
            OnPremiseJiraStatusDTO onPremiseJiraStatusDTO = new OnPremiseJiraStatusDTO();
            onPremiseJiraStatusDTO.setSelf(status.getSelf().toString());
            onPremiseJiraStatusDTO.setId(status.getId());
            onPremiseJiraStatusDTO.setName(status.getName());
            onPremiseJiraStatusDTO.setDescription(status.getDescription());
            onPremiseJiraStatusDTOList.add(onPremiseJiraStatusDTO);
        }

        return onPremiseJiraStatusDTOList;
    }

}
