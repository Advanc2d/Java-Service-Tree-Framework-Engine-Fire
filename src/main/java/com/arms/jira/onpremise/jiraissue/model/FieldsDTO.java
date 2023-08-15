package com.arms.jira.onpremise.jiraissue.model;

import com.arms.jira.onpremise.jiraissuepriority.model.OnPremiseJiraIssuePriorityDTO;
import com.arms.jira.onpremise.jiraissueresolution.model.OnPremiseJiraIssueResolutionDTO;
import com.arms.jira.onpremise.jiraissuestatus.model.OnPremiseJiraIssueStatusDTO;
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

    private Project project; // 프로젝트

    private IssueType issuetype; // 이슈 타입

    private String summary; // 요약

    private String description; // 설명

    private Reporter reporter; // 보고자

    private Assignee assignee; // 담당자

    private List<String> labels; // 라벨

    private List<IssueLink> issuelinks; // 연결된 이슈

    private List<OnPremiseJiraIssueDTO> subtasks; // sub task

    private OnPremiseJiraIssuePriorityDTO priority; // 우선순위

    private OnPremiseJiraIssueStatusDTO status; // 상태값

    private OnPremiseJiraIssueResolutionDTO resolution; // 해결책

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Project {
        private String self;
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
        private String self;
        private String id;
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
        private String self;
        private String id;
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
        private String self;
        private String id;
        private String name;
        private String inward;
        private String outward;
    }

}
