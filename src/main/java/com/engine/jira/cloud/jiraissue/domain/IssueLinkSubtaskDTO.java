package com.engine.jira.cloud.jiraissue.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IssueLinkSubtaskDTO {
    private CloudJiraIssueDTO nodeIssue;
    private List<CloudJiraIssueDTO> inwardIssues;
    private List<CloudJiraIssueDTO> outwardIssues;
    private List<CloudJiraIssueDTO> subtasks;
}
