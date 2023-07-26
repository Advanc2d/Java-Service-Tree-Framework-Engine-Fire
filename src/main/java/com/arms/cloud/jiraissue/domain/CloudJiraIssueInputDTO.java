package com.arms.cloud.jiraissue.domain;

import java.util.List;
import java.util.Map;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CloudJiraIssueInputDTO {
    private FieldsDTO fields;
    private Map<String, Object> update;
    private List<String> watchers;
}
