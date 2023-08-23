package com.arms.jira.cloud.jiraproject.service;


import com.arms.jira.cloud.CloudJiraUtils;
import com.arms.jira.cloud.jiraproject.model.CloudJiraProjectDTO;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@AllArgsConstructor
@Service
public class CloudJiraProjectImpl implements CloudJiraProject {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JiraInfo jiraInfo;


	@Override
	public CloudJiraProjectDTO getProjectData(Long connectId, String projectKey) throws Exception {
		String endpoint = "/rest/api/3/project/"+ projectKey;

		JiraInfoDTO found = jiraInfo.checkInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        CloudJiraProjectDTO project = CloudJiraUtils.get(webClient, endpoint, CloudJiraProjectDTO.class).block();

        logger.info(project.toString());

        return project;
	}

	@Override
	public List<CloudJiraProjectDTO> getProjectList(Long connectId) throws Exception {

		String endpoint = "/rest/api/3/project";

		JiraInfoDTO found = jiraInfo.checkInfo(connectId);

		if (found == null) {
			// throw Exception e; ControllerAdvice 오류 처리
		}

		WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getUserId(), found.getPasswordOrToken());

		// ObjectMapper objectMapper = new ObjectMapper();
		// String response = CloudJiraUtils.get(webClient, endpoint, String.class).block();
		// List<CloudJiraProjectDTO> projects = objectMapper.readValue(response, new TypeReference<List<CloudJiraProjectDTO>>() {});

		List<CloudJiraProjectDTO> projects = CloudJiraUtils.get(webClient, endpoint, new ParameterizedTypeReference<List<CloudJiraProjectDTO>>() {}).block();

        logger.info(projects.toString());

	    return projects;
	}
}
