package com.arms.jira.onpremise.jiraissue.model;

import com.arms.jira.cloud.jiraissue.model.FieldsDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnPremiseJiraIssueDTO {
    private String key;
    private String self;

    private FieldsDTO fields;
}
