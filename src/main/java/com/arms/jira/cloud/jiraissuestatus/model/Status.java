package com.arms.jira.cloud.jiraissuestatus.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Status {
    private String id;
    private String name;
    private String description;
    private String self;
}
