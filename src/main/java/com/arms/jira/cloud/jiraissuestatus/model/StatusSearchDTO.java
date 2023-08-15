package com.arms.jira.cloud.jiraissuestatus.model;
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
public class StatusSearchDTO {
    private Integer maxResults;
    private Integer startAt;
    private Integer total;
    private boolean isLast;
    private List<Status> values;

}