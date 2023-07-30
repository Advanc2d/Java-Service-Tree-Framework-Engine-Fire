package com.arms.config;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class JiraConfig {

    @Value("${arms.jira.baseurl}")
    public String jiraUrl;

    @Value("${arms.jira.id}")
    public String jiraID;

    @Value("${arms.jira.pass}")
    public String jiraPass;

    public JiraRestClient getJiraRestClient() throws URISyntaxException, IOException {

        final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

        return factory.createWithBasicHttpAuthentication(new URI(jiraUrl), jiraID, jiraPass);

    }

}
