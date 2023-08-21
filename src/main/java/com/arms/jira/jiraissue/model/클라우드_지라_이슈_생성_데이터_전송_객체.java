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
public class 클라우드_지라_이슈_생성_데이터_전송_객체 {

    private 클라우드_지라_이슈_필드_데이터_전송_객체 fields;

}
