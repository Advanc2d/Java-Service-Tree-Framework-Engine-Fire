package com.engine.jira.cloud.jiraissue.model;

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
