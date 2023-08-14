package com.arms.jira.info.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JiraInfoDTO {
    private Long connectId;
    private String userId;
    private String passwordOrToken;
    private String uri;
}
