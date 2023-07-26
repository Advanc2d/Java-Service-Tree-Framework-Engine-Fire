package com.arms.cloud.jiraissue.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldsDTO {
    private String statuscategorychangedate;
    private List<Object> fixVersions;
    private Object resolution;
    private String lastViewed;
    private Object customfield_10060;
    private Object customfield_10061;
    private Priority priority;
    private List<Object> labels;
    private Object timeestimate;
    private Object aggregatetimeoriginalestimate;
    private List<Object> versions;
    private List<IssueLink> issuelinks;
    private Object assignee;
    private Status status;
    private List<Object> components;
    private Object customfield_10050;
    private Object customfield_10051;
    private Object customfield_10052;
    private Object customfield_10053;
    private Object customfield_10054;
    private CustomField10055 customfield_10055;
    private CustomField10056 customfield_10056;
    private CustomField10057 customfield_10057;
    private CustomField10058 customfield_10058;
    private Object customfield_10049;
    private Object aggregatetimeestimate;
    private User creator;
    private List<Object> subtasks;
    private Object customfield_10040;
    private Object customfield_10041;
    private Object customfield_10042;
    private User reporter;
    private List<Object> customfield_10043;
    private AggregateProgress aggregateprogress;
    private Object customfield_10044;
    private Object customfield_10045;
    private Object customfield_10046;
    private Object customfield_10047;
    private Object customfield_10048;
    private Object customfield_10038;
    private Object customfield_10039;
    private Progress progress;
    private Votes votes;
    private Worklog worklog;
    private IssueType issuetype;
    private Integer timespent;
    private Object customfield_10030;
    private Project project;
    private Object customfield_10031;
    private Object customfield_10032;
    private List<Object> customfield_10033;
    private Object aggregatetimespent;
    private Object customfield_10034;
    private Object customfield_10035;
    private CustomField10037 customfield_10037;
    private Object customfield_10027;
    private Object customfield_10028;
    private Object customfield_10029;
    private Object resolutiondate;
    private Integer workratio;
    private IssueRestriction issuerestriction;
    private Watches watches;
    private String created;
    private Object customfield_10020;
    private Object customfield_10021;
    private Object customfield_10022;
    private Object customfield_10023;
    private Object customfield_10024;
    private Object customfield_10025;
    private Object customfield_10026;
    private Object customfield_10016;
    private Object customfield_10017;
    private CustomField10018 customfield_10018;
    private String customfield_10019;
    private String updated;
    private String timeoriginalestimate;
    private Description description;
    private Object customfield_10010;
    private Object customfield_10014;
    private Object customfield_10015;
    private TimeTracking timetracking;
    private Object customfield_10005;
    private Object customfield_10006;
    private Object customfield_10007;
    private Object security;
    private List<Object> attachment;
    private Object customfield_10008;
    private Object customfield_10009;
    private String summary;
    private Object customfield_10001;
    private List<Object> customfield_10002;
    private Object customfield_10003;
    private Object customfield_10004;
    private Object environment;
    private Object duedate;
    private Comment comment;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Priority {
        private String self;
        private String iconUrl;
        private String name;
        private String id;
    }
    
    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IssueLink {
        private String id;
        private String self;
        private Type type;
        private InwardIssue inwardIssue;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Type {
        private String id;
        private String name;
        private String inward;
        private String outward;
        private String self;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InwardIssue {
        private String id;
        private String key;
        private String self;
        private Fields fields;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Fields {
        private String summary;
        private Status status;
        private Priority priority;
        private IssueType issuetype;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Status {
        private String self;
        private String description;
        private String iconUrl;
        private String name;
        private String id;
        private StatusCategory statusCategory;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatusCategory {
        private String self;
        private Integer id;
        private String key;
        private String colorName;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CustomField10055 {
        private String id;
        private String name;
        private Links _links;
        private List<Object> completedCycles;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CustomField10056 {
        private String id;
        private String name;
        private Links _links;
        private List<Object> completedCycles;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CustomField10057 {
        private String id;
        private String name;
        private Links _links;
        private List<Object> completedCycles;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CustomField10058 {
        private String id;
        private String name;
        private Links _links;
        private List<Object> completedCycles;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CustomField10037 {
        private String languageCode;
        private String displayName;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IssueRestriction {
        private IssueRestrictions issuerestrictions;
        private Boolean shouldDisplay;
    }
    
    @Getter
    @Setter
    @Builder
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IssueRestrictions {
        // Define properties if available.
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Watches {
        private String self;
        private Integer watchCount;
        private Boolean isWatching;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class User {
        private String self;
        private String accountId;
        private String emailAddress;
        private AvatarUrls avatarUrls;
        private String displayName;
        private Boolean active;
        private String timeZone;
        private String accountType;

        private String id;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AvatarUrls {
        private String _48x48;
        private String _24x24;
        private String _16x16;
        private String _32x32;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AggregateProgress {
        private Integer progress;
        private Integer total;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Progress {
        private Integer progress;
        private Integer total;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Votes {
        private String self;
        private Integer votes;
        private Boolean hasVoted;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Worklog {
        private Integer startAt;
        private Integer maxResults;
        private Integer total;
        private List<Object> worklogs;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IssueType {
        private String self;
        private String id;
        private String description;
        private String iconUrl;
        private String name;
        private Boolean subtask;
        private Integer avatarId;
        private Integer hierarchyLevel;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Project {
        private String self;
        private String id;
        private String key;
        private String name;
        private String projectTypeKey;
        private Boolean simplified;
        private AvatarUrls avatarUrls;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Comment {
        private List<Object> comments;
        private String self;
        private Integer maxResults;
        private Integer total;
        private Integer startAt;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CustomField10018 {
        private String id;
        private String name;
        private Links _links;
        private List<Object> completedCycles;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TimeTracking {
        // Define properties if available.
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Links {
        private String self;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Description {
        private int version;
        private String type;
        private List<Content> content;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Content {
        private String type;
        private List<Paragraph> content;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Paragraph {
        private String type;
        private String text;
    }
}
