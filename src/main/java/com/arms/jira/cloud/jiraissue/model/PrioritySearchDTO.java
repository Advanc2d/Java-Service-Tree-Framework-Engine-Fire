package com.arms.jira.cloud.jiraissue.model;
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
public class PrioritySearchDTO {
    private Integer maxResults;
    private Integer startAt;
    private Integer total;
    private boolean isLast;
    private List<Priority> values;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Priority {
        private String self;
        private String id;
        private String name;
        private String description;
        private boolean isDefault;
    }
}