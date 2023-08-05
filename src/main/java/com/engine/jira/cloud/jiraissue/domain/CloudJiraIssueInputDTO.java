package com.engine.jira.cloud.jiraissue.domain;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CloudJiraIssueInputDTO {
    private FieldsDTO fields;
}
