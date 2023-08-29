package com.arms.jira.jiraissue.model;

import com.arms.jira.jiraissueresolution.model.지라_이슈_해결책_데이터_전송_객체;
import com.arms.jira.jiraissuestatus.model.지라_이슈_상태_데이터_전송_객체;
import com.arms.jira.jiraissuetype.model.지라_이슈_유형_데이터_전송_객체;
import com.arms.jira.jirapriority.model.지라_이슈_우선순위_데이터_전송_객체;
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
public class 지라이슈필드_데이터 {

    private 지라프로젝트_데이터 project;

    private 지라_이슈_유형_데이터_전송_객체 issuetype;

    private 지라사용자_데이터 creator;

    private 지라사용자_데이터 reporter;

    private 지라사용자_데이터 assignee;

    private List<String> labels;

    private 지라_이슈_우선순위_데이터_전송_객체 priority;

    private 지라_이슈_상태_데이터_전송_객체 status;

    private 지라_이슈_해결책_데이터_전송_객체 resolution;

    private String resolutiondate;

    private String created;

    private List<지라이슈워크로그_데이터> worklogs;

    private Integer timespent;

    private List<지라이슈버전_데이터> fixVersions;

}
