package com.arms.jira.jiraissue.model;

import com.arms.jira.jiraissueresolution.model.지라이슈_해결책_데이터;
import com.arms.jira.jiraissuestatus.model.지라이슈상태_데이터;
import com.arms.jira.jiraissuetype.model.지라이슈유형_데이터;
import com.arms.jira.jirapriority.model.지라이슈_우선순위_데이터;
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
public class 지라이슈생성필드_데이터 {

    private 프로젝트 project;

    private 지라이슈유형_데이터 issuetype;

    private String summary;

    private String description;

    private 보고자 reporter;

    private 담당자 assignee;

    private List<String> labels;

    private 지라이슈_우선순위_데이터 priority;

    private 지라이슈상태_데이터 status;

    private 지라이슈_해결책_데이터 resolution;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 프로젝트 {
        private String self;
        private String id;
        private String key;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 보고자 {
        private String name;
        private String emailAddress;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 담당자 {
        private String name;
        private String emailAddress;
    }

}
