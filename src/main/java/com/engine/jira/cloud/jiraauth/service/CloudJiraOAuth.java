package com.engine.jira.cloud.jiraauth.service;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface  CloudJiraOAuth {
    public ResponseEntity cloudJiraAuthorization() throws IOException;
    public String cloudJiraAccessToken(String code, HttpServletRequest request);
}
