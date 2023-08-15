package com.arms.jira.onpremise.jiraissuestatus.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnPremiseJiraIssueStatusDTO {

    private  String self;

    private String id;

    private String name;

    private String description;
}
