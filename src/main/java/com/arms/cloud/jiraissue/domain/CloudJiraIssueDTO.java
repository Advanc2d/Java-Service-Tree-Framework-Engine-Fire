package com.arms.cloud.jiraissue.domain;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudJiraIssueDTO {
    private String expand;
    private String id;
    private String self;
    private String key;

    private FieldsDTO fields;

    private List<Watchers> watchers;

    // 수현이가 한 부분에서 수정
    // private ProjectDTO project;
    // private IssueTypeDTO issuetype;
    // private String summary;
    // private String description;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Watchers {
        private Integer status;
        private WarningCollection warningCollection;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WarningCollection {
        private List<String> warnings;
    }
}
