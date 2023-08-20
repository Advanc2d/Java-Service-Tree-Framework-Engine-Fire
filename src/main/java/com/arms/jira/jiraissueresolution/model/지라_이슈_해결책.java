package com.arms.jira.jiraissueresolution.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 지라_이슈_해결책 {
    private String self;
    private String id;
    private String name;
    private String description;
    private boolean isDefault;
}
