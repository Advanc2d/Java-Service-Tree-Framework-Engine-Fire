package com.arms.jira.onpremise.jiraissueresolution.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnPremiseJiraIssueResolutionDTO {
    private String self;
    private String id;
    private String name;
    private String description;
}
