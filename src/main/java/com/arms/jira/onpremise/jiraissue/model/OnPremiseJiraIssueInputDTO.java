package com.arms.jira.onpremise.jiraissue.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OnPremiseJiraIssueInputDTO {
    private FieldsDTO fields;
}
