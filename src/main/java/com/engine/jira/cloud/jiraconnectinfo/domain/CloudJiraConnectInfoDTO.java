package com.engine.jira.cloud.jiraconnectinfo.domain;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CloudJiraConnectInfoDTO {
    private String connectId;
    private String email;
    private String token;
    private String uri;
}
