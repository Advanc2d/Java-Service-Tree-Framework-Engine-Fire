package com.arms.jira.jiraissueresolution.model;

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
public class 클라우드_지라_이슈_해결책_전체_데이터_전송_객체 {
    private Integer maxResults;
    private Integer startAt;
    private Integer total;
    private boolean isLast;
    private List<지라_이슈_해결책> values;
}
