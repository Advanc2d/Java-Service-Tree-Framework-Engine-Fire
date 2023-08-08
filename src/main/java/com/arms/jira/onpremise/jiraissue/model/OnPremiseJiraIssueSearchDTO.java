package com.arms.jira.onpremise.jiraissue.model;

import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueDTO;
import com.arms.jira.cloud.jiraissue.model.FieldsDTO;
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
public class OnPremiseJiraIssueSearchDTO {
    private Integer startAt;
    private Integer maxResults;
    private Integer total;
    private List<CloudJiraIssueDTO> issues;
}
