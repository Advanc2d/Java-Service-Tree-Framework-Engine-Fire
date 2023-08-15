package com.arms.jira.onpremise.jiraissue.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnPremiseJiraIssueDTO {

    private String id;

    private String key;

    private String self;

    private FieldsDTO fields;

    private List<OnPremiseJiraIssueDTO> issues;

    public OnPremiseJiraIssueDTO(String id, String key, String self) {
        this.key = key;
        this.id = id;
        this.self = self;
        this.issues = new ArrayList<>();
    }
}
