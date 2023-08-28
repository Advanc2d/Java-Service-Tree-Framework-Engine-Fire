package com.arms.jira.cloud.jiraissuetype.service;

import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeDTO;
import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeInputDTO;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.model.JiraInfoEntity;
import com.arms.jira.info.service.JiraInfo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service("cloudJiraIssueType")
@AllArgsConstructor
public class CloudJiraIssueTypeImpl implements CloudJiraIssueType {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JiraInfo jiraInfo;

    @Override
    public List<CloudJiraIssueTypeDTO> getIssueTypeListAll(Long connectId) throws Exception {

        String endpoint = "/rest/api/3/issuetype";

        JiraInfoDTO found = jiraInfo.checkInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        List<CloudJiraIssueTypeDTO> issueTypes = CloudJiraUtils.get(webClient, endpoint,
                                                new ParameterizedTypeReference<List<CloudJiraIssueTypeDTO>>() {}).block();

        logger.info(issueTypes.toString());

        return issueTypes;
    }

    @Override
    public List<CloudJiraIssueTypeDTO> getIssueTypeListByProjectId(Long connectId, String projectId) throws Exception {

        String endpoint = "/rest/api/3/issuetype/project?projectId=" + projectId;

        JiraInfoDTO found = jiraInfo.checkInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        List<CloudJiraIssueTypeDTO> issueTypes = CloudJiraUtils.get(webClient, endpoint,
                                        new ParameterizedTypeReference<List<CloudJiraIssueTypeDTO>>() {}).block();

        logger.info(issueTypes.toString());

        return issueTypes;
    }

    @Transactional
    @Override
    public CloudJiraIssueTypeDTO createIssueType(Long connectId,
                                                 CloudJiraIssueTypeInputDTO cloudJiraIssueTypeInputDTO)
            throws Exception {

        String endpoint = "/rest/api/3/issuetype";

        JiraInfoDTO found = jiraInfo.checkInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        CloudJiraIssueTypeDTO addCloudJirarIssueTypeDTO = CloudJiraUtils.post(webClient, endpoint,
                cloudJiraIssueTypeInputDTO, CloudJiraIssueTypeDTO.class).block();

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        JiraInfoEntity jiraInfoEntity = modelMapper.map(found, JiraInfoEntity.class);

        if (jiraInfoEntity != null) {
            jiraInfoEntity.setIssueId(addCloudJirarIssueTypeDTO.getId());
            jiraInfoEntity.setIssueName(addCloudJirarIssueTypeDTO.getName());
            jiraInfoEntity.setSelf(addCloudJirarIssueTypeDTO.getSelf());
        }

        JiraInfoEntity returnEntity = jiraInfo.saveIssueTypeInfo(jiraInfoEntity);

        if (returnEntity == null) {
            return null;
        }

        logger.info(addCloudJirarIssueTypeDTO.toString());

        return addCloudJirarIssueTypeDTO;
    }

}
