package com.arms.cloud.jiraconnectinfo.domain;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CloudJiraConnectInfoDTO {
    private String id;
    private String token;
    private String url;
}
