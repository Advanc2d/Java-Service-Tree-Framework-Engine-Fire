package com.arms.cloud.jiraissuetypescheme.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudJiraIssueTypeSchemeMappingValueDTO {
    private String issueTypeSchemeId;
    private String issueTypeId;
}
