package com.arms.jira.onpremise.jiraissuetype.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OnPremiseJiraIssueTypeInputDTO {
    private String description;
    private String name;
    private String type; // 표준 이슈 유형(standard), 하위 작업 이슈 유형(subtask)
}