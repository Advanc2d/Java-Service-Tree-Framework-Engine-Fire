package com.arms.jira.onpremise.jirastatus.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.onpremise.OnPremiseJiraUtils;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.atlassian.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service("onPremiseJiraStatus")
public class OnPremiseJiraStatusImpl implements OnPremiseJiraStatus{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public JsonNode getStatusList(Long connectId) throws Exception {
        JiraInfoDTO info = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                info.getUserId(),
                info.getPasswordOrToken());

        // 상태 리스트 조회 로직 추가
        Promise<Iterable<Status>> statusesPromise = restClient.getMetadataClient().getStatuses();
        Iterable<Status> statuses = statusesPromise.claim();

        // ObjectMapper 객체 생성
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode statusArray = mapper.createArrayNode();

        // 상태 정보를 JsonNode 형태로 만들기
        for (Status status : statuses) {
            ObjectNode statusObject = mapper.createObjectNode();
            statusObject.put("id", status.getId());
            statusObject.put("name", status.getName());
            statusObject.put("description", status.getDescription());
            statusArray.add(statusObject);
        }

        // ObjectNode 객체를 만들고 상태 리스트를 추가한다.
        ObjectNode response = mapper.createObjectNode();
        response.set("statuses", statusArray);

        return response;
    }
}
