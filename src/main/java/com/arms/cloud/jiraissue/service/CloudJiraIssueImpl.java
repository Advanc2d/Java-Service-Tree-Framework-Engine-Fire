package com.arms.cloud.jiraissue.service;

import com.arms.cloud.jiraissue.domain.CloudJiraIssueDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueInputDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueSearchDTO;
import com.arms.config.CloudJiraConfig;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service("cloudJiraIssue")
public class CloudJiraIssueImpl implements CloudJiraIssue {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("cloudJiraConfig")
    private CloudJiraConfig cloudJiraConfig;

    @Override
    public CloudJiraIssueSearchDTO getIssueSearch(String projectKeyOrId) {
        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

        String endpoint = "/rest/api/3/search?jql=project=" + projectKeyOrId;

        CloudJiraIssueSearchDTO response = jiraWebClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(CloudJiraIssueSearchDTO.class).block();

        logger.info(response.toString());
        return response;
    }

    @Override
    public CloudJiraIssueDTO getIssue(String issueKeyOrId) {
        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

        String endpoint = "/rest/api/3/issue/" + issueKeyOrId;

        CloudJiraIssueDTO response = jiraWebClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(CloudJiraIssueDTO.class).block();

        String jsonResponse = response.toString();
        logger.info(jsonResponse);

        return response;
    }

    @Override
    public String createIssue(CloudJiraIssueInputDTO cloudJiraIssueInputDTO) throws Exception {

        final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();
        boolean updateHistory = true;
        boolean applyDefaultValues = false;
        boolean skipAutoWatch = true;

        String param = "?updateHistory=" + updateHistory + "&applyDefaultValues=" + applyDefaultValues + "&skipAutoWatch=" + skipAutoWatch;
        String endpoint = "/rest/api/3/issue"+param;

        Mono<String> response = jiraWebClient.post()
                .uri(endpoint)
                .bodyValue(cloudJiraIssueInputDTO)
                .retrieve()
                .bodyToMono(String.class);

        String jsonResponse = response.block();

        return jsonResponse;
    }

}
