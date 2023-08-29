package com.arms.jira.onpremise.jiraissuestatus.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.onpremise.jiraissuestatus.model.OnPremiseJiraIssueStatusDTO;
import com.arms.jira.utils.지라유틸;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Status;
import io.atlassian.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@Service("onPremiseJiraStatus")
public class OnPremiseJiraIssueStatusImpl implements OnPremiseJiraIssueStatus {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<OnPremiseJiraIssueStatusDTO> getStatusList(Long connectId) throws Exception {
        JiraInfoDTO info = 지라연결_서비스.checkInfo(connectId);
        JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(info.getUri(),
                info.getUserId(),
                info.getPasswordOrToken());

        // 상태 리스트 조회 로직 추가
        Promise<Iterable<Status>> statusesPromise = restClient.getMetadataClient().getStatuses();
        Iterable<Status> statuses = statusesPromise.claim();

        List<OnPremiseJiraIssueStatusDTO> onPremiseJiraIssueStatusDTOList = new ArrayList<>();
        for (Status status : statuses) {
            OnPremiseJiraIssueStatusDTO onPremiseJiraIssueStatusDTO = new OnPremiseJiraIssueStatusDTO();
            onPremiseJiraIssueStatusDTO.setSelf(status.getSelf().toString());
            onPremiseJiraIssueStatusDTO.setId(status.getId().toString());
            onPremiseJiraIssueStatusDTO.setName(status.getName());
            onPremiseJiraIssueStatusDTO.setDescription(status.getDescription());
            onPremiseJiraIssueStatusDTOList.add(onPremiseJiraIssueStatusDTO);
        }

        return onPremiseJiraIssueStatusDTOList;
    }

}
