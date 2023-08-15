package com.arms.jira.cloud.jiraissuetype.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.elasticsearch.models.EsJiraIssueType;
import com.arms.elasticsearch.services.JiraIssueTypeService;
import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeDTO;
import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeInputDTO;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.model.JiraInfoEntity;
import com.arms.jira.info.service.JiraInfo;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service("cloudJiraIssueType")
@AllArgsConstructor
public class CloudJiraIssueTypeImpl implements CloudJiraIssueType {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JiraInfo jiraInfo;

    @Autowired
    @Qualifier("JiraIssueTypeService")
    private JiraIssueTypeService jiraIssueTypeService;

    @Override
    public List<CloudJiraIssueTypeDTO> getIssueTypeListAll(Long connectId) throws Exception {

        String endpoint = "/rest/api/3/issuetype";

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        List<CloudJiraIssueTypeDTO> issueTypes = CloudJiraUtils.get(webClient, endpoint, List.class).block();

        logger.info(issueTypes.toString());

        return issueTypes;
    }

    public Mono<List<CloudJiraIssueTypeDTO>> getNonBlockIssueTypeListAll(Long connectId) throws Exception {

        String endpoint = "/rest/api/3/issuetype";

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());
        return CloudJiraUtils.get(webClient, endpoint, new ParameterizedTypeReference<>() {
        });
    }


    @Override
    public List<CloudJiraIssueTypeDTO> getIssueTypeListByProjectId(Long connectId, String projectId) throws Exception {

        String endpoint = "/rest/api/3/issuetype/project?projectId=" + projectId;

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        List<CloudJiraIssueTypeDTO> issueTypes = CloudJiraUtils.get(webClient, endpoint, List.class).block();

        logger.info(issueTypes.toString());

        return issueTypes;
    }

    @Transactional
    @Override
    public CloudJiraIssueTypeDTO createIssueType(Long connectId,
                                                 CloudJiraIssueTypeInputDTO cloudJiraIssueTypeInputDTO)
            throws Exception {

        String endpoint = "/rest/api/3/issuetype";

        JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
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

    @Override
    public void saveIssueTypeByUsers() throws Exception {

        String endpoint = "/rest/api/3/issuetype";
        List<JiraInfoDTO> founds = jiraInfo.loadConnectInfos();

        for (JiraInfoDTO found : founds) {


            WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

            List<EsJiraIssueType> result = new ArrayList<>();

            CloudJiraUtils.get(webClient, endpoint,
                    new ParameterizedTypeReference<List<CloudJiraIssueTypeDTO>>() {})
                .subscribe(cloudJiraIssueTypes -> {
                        cloudJiraIssueTypes.stream()
                            .forEach(cloudJiraIssueType ->
                                {
                                    EsJiraIssueType esJiraIssueType = modelMapper.map(cloudJiraIssueType, EsJiraIssueType.class);
                                    esJiraIssueType.generateIdByUrl(found.getUri());
                                    result.add(esJiraIssueType);
                                }
                            );
                    },
                    error -> {
                        logger.error(error.getMessage());
                    },
                    () -> {
                        jiraIssueTypeService.createJiraIssueTypeIndexBulk(result);
                    }
                );

        }

    }
}
