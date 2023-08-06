package com.engine.jira.cloud.jiraauth.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloudJiraTokenRequestDTO {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String refresh_token;
}
