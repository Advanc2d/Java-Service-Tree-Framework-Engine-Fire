package com.arms.jira.onpremise.jirastatus.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnPremiseJiraStatusDTO {

    private  String self;

    private Long id;

    private String name;

    private String description;
}
