package com.arms.cloud.jiraproject.service;



import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.cloud.CloudJiraUtils;
import com.arms.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoDTO;
import com.arms.cloud.jiraconnectinfo.service.CloudJiraConnectInfo;
import com.arms.cloud.jiraproject.domain.CloudJiraProjectDTO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CloudJiraProjectImpl implements CloudJiraProject {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CloudJiraConnectInfo cloudJiraConnectInfo;

	@Override
	public CloudJiraProjectDTO getProjectData(String projectKey, String connectId) throws Exception {
		String endpoint = "/rest/api/3/project/"+ projectKey;
		CloudJiraConnectInfoDTO found = cloudJiraConnectInfo.loadConnectInfo(connectId);
        WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getEmail(), found.getToken());

        CloudJiraProjectDTO project = CloudJiraUtils.get(webClient, endpoint, CloudJiraProjectDTO.class).block();

        logger.info(project.toString());

        return project;
	}

	@Override
	public List<CloudJiraProjectDTO> getProjectList(String connectId) throws Exception {

		String endpoint = "/rest/api/3/project";

		CloudJiraConnectInfoDTO found = cloudJiraConnectInfo.loadConnectInfo(connectId);


		WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getEmail(), found.getToken());

		// ObjectMapper objectMapper = new ObjectMapper();
		// String response = CloudJiraUtils.get(webClient, endpoint, String.class).block();
		// List<CloudJiraProjectDTO> projects = objectMapper.readValue(response, new TypeReference<List<CloudJiraProjectDTO>>() {});

		List<CloudJiraProjectDTO> projects = CloudJiraUtils.get(webClient, endpoint, List.class).block();

        logger.info(projects.toString());

	    return projects;
	}
}
