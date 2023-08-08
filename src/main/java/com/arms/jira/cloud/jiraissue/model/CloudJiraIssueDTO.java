package com.arms.jira.cloud.jiraissue.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudJiraIssueDTO {
    private String id;
    private String key;
    private String self;

    private FieldsDTO fields;
}
