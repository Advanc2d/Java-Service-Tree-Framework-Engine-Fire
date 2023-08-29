package com.arms.jira.jiraissue.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 지라이슈버전_데이터 {

    private String self;

    private String id;

    private String name;

    private String description;

    private boolean archived;

    private boolean released;

    private String releaseDate;

}
