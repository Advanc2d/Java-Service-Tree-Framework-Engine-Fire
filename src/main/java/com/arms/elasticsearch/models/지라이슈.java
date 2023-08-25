package com.arms.elasticsearch.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
@Document(indexName = "jiraissue")
public class 지라이슈 {

    @Id
    private String id; // Elasticsearch의 문서 식별자

    public void generateId() {
        if (timestamp == null) {
            this.timestamp = new Date();
        }
        this.id = this.jira_server_id + "_" + this.project.getKey() + "_" + this.key;
    }

    private Long jira_server_id;

    @Field(type = FieldType.Date, name = "@timestamp")
    private Date timestamp;

    @Field(type = FieldType.Keyword, name = "key")
    private String key;

    @Field(type = FieldType.Text, name = "self")
    private String self;


    private 지라이슈.프로젝트 project;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 프로젝트 {
        @Field(type = FieldType.Text, name = "project_self")
        private String self;

        @Field(type = FieldType.Text, name = "project_id")
        private String id;

        @Field(type = FieldType.Text, name = "project_key")
        private String key;

        @Field(type = FieldType.Text, name = "project_name")
        private String name;
    }


    // 특정 프로젝트의 전체 이슈 조회 시 사용
    private List<지라이슈> issues;

}
