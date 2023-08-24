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
public class 클라우드_지라_이슈_필드_데이터_전송_객체 {

    private 지라_이슈_필드_데이터_전송_객체.프로젝트 project;

    private 지라_이슈_유형_데이터_전송_객체 issuetype;

    private String summary;

    private 내용 description;

    private 사용자 reporter;

    private 사용자 assignee;

    private List<String> labels;

    private List<지라_이슈_필드_데이터_전송_객체.연결된_이슈> issuelinks;

    private List<클라우드_지라_이슈_데이터_전송_객체> subtasks;

    private 지라_이슈_우선순위_데이터_전송_객체 priority;

    private 지라_이슈_상태_데이터_전송_객체 status;

    private 지라_이슈_해결책_데이터_전송_객체 resolution;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 내용 {
        private List<콘텐츠> content;
        private String type;
        private Integer version;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 콘텐츠 {
        private List<콘텐츠_아이템> content;
        private String type;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 콘텐츠_아이템 {
        private String text;
        private String type;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 사용자 {
        private String accountId;
        private String emailAddress;
    }

}
