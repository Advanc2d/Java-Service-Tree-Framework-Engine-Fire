package com.arms.cloud.jiraproject.service;



import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.cloud.jiraproject.domain.CloudJiraProjectDTO;
import com.arms.config.CloudJiraConfig;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CloudJiraProjectImpl implements CloudJiraProject {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private CloudJiraConfig cloudJiraConfig;

	@Override
	public CloudJiraProjectDTO getProjectData(String projectKey) throws Exception {
        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

        String endpoint = "/rest/api/3/project/"+ projectKey;

         CloudJiraProjectDTO project = jiraWebClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(CloudJiraProjectDTO.class).block();

        logger.info(project.toString());

        return project;
	}

	@Override
	public List<CloudJiraProjectDTO> getProjectList() throws Exception {
	       final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

	        String endpoint = "/rest/api/3/project";

	        List<CloudJiraProjectDTO> projects = jiraWebClient.get()
	                .uri(endpoint)
	                .retrieve()
	                .bodyToMono(List.class).block();

	        logger.info(projects.toString());
	        
	        return projects;
	}
}
