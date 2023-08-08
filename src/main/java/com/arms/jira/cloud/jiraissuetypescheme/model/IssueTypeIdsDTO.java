package com.arms.jira.cloud.jiraissuetypescheme.model;

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
public class IssueTypeIdsDTO {
    private List<String> issueTypeIds;
}
