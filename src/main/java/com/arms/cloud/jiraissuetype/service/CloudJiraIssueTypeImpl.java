package com.arms.cloud.jiraissuetype.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeDTO;
import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeInputDTO;
import com.arms.config.CloudJiraConfig;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CloudJiraIssueTypeImpl implements CloudJiraIssueType {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CloudJiraConfig cloudJiraConfig;

	@Override
	public List<CloudJiraIssueTypeDTO> getIssueTypeList() throws Exception {
                final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

                String endpoint = "/rest/api/3/issuetype";

                List<CloudJiraIssueTypeDTO> issueTypes = jiraWebClient.get()
                        .uri(endpoint)
                        .retrieve()
                        .bodyToMono(List.class).block();

                logger.info(issueTypes.toString());

                return issueTypes;
	}

	@Override
	public CloudJiraIssueTypeDTO createIssueType(CloudJiraIssueTypeInputDTO cloudJiraIssueTypeInputDTO)
			throws Exception {
                final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

                String endpoint = "/rest/api/3/issuetype";

                CloudJiraIssueTypeDTO addCloudJirarIssueTypeDTO = jiraWebClient.post()
                        .uri(endpoint)
                        .bodyValue(cloudJiraIssueTypeInputDTO)
                        .retrieve()
                        .bodyToMono(CloudJiraIssueTypeDTO.class).block();

                logger.info(addCloudJirarIssueTypeDTO.toString());

                return addCloudJirarIssueTypeDTO;
	}
   
    

}
