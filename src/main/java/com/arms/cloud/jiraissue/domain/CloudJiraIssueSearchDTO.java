package com.arms.cloud.jiraissue.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudJiraIssueSearchDTO {
    private String expand;
    private Integer startAt;
    private Integer maxResults;
    private Integer total;
    private List<CloudJiraIssueDTO> issues;
}
