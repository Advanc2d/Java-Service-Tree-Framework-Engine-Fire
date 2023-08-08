package com.arms.jira.cloud.jiraproject.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComponentDTO {

    // non use
    // 컴포넌트는 관리 대상이 아닙니다.
    private String self;
    private String id;
    private String name;
    private String description;
    private boolean isAssigneeTypeValid;
}
