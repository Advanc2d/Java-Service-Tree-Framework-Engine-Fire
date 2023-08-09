package com.arms.jira.onpremise.jiraissue.model;

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
public class FieldsDTO {

    // 필수 및 공통 필드
    private Project project; // 프로젝트

    private IssueType issuetype; // 이슈 타입

    private String summary; // 요약

    private String description; // 설명

    private Reporter reporter; // 보고자

    private Assignee assignee; // 담당자

    private List<String> labels; // 라벨

    private List<IssueLink> issuelinks; // 연결된 이슈

    // 추가
    private Priority priority; // 우선순위
    private List<OnPremiseJiraIssueDTO> subtasks; // sub task

    private Status status; // 상태값
    private Status resolution;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Project {
        private String id;
        private String key;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IssueType {
        private String id;
        private String key;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Reporter {
        private String name;
        private String emailAddress;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Assignee {
        private String name;
        private String emailAddress;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IssueLink {
        private String id;
        private String self;
        private Type type;
        private OnPremiseJiraIssueDTO inwardIssue;
        private OnPremiseJiraIssueDTO outwardIssue;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Type {
        private String id;
        private String name;
        private String inward;
        private String outward;
        private String self;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Status {
        private String id;
        private String name;
        private String description;
        private String self;
    }

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
    }

}
