package com.arms.cloud.jiraissue.domain;

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
    private String expand;
    private String id;
    private String key;
    private String self;

    private FieldsDTO fields;
}
