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
public class ResolutionSearchDTO {
    private Integer maxResults;
    private Integer startAt;
    private Integer total;
    private boolean isLast;
    private List<Resolution> values;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Resolution {
        private String self;
        private String id;
        private String name;
        private String description;
        private boolean isDefault;
    }
}