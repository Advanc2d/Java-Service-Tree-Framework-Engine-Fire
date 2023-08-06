package com.engine.jira.cloud.jiraissuetype.service;

import com.engine.jira.cloud.CloudJiraUtils;
import com.engine.jira.cloud.jiraissuetype.dao.CloudJiraIssueTypeJpaRepository;
import com.engine.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeDTO;
import com.engine.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeEntity;
import com.engine.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeInputDTO;
import com.engine.jira.info.model.JiraInfoDTO;
import com.engine.jira.info.model.JiraInfoEntity;
import com.engine.jira.info.service.JiraInfo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service("cloudJiraIssueType")
public class CloudJiraIssueTypeImpl implements CloudJiraIssueType {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Autowired
        private CloudJiraIssueTypeJpaRepository cloudJiraIssueTypeJpaRepository;
        @Autowired
        private ModelMapper modelMapper;
        @Autowired
        private JiraInfo jiraInfo;

        @Transactional
	@Override
	public CloudJiraIssueTypeDTO createIssueType(String connectId, 
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
        public List<CloudJiraIssueTypeDTO> getIssueTypeListByCloud(String connectId) throws Exception {

                String endpoint = "/rest/api/3/issuetype";

                JiraInfoDTO found = jiraInfo.loadConnectInfo(connectId);
                WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

                List<CloudJiraIssueTypeDTO> issueTypes = CloudJiraUtils.get(webClient, endpoint, List.class).block();

                logger.info(issueTypes.toString());

                return issueTypes;
	}

}
