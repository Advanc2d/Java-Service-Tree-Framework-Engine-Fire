package com.arms.elasticsearch.models;

import com.arms.jira.jiraissue.model.지라_이슈_데이터_전송_객체;
import com.arms.jira.jiraissue.model.지라_이슈_필드_데이터_전송_객체;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "jiraissue")
public class 지라이슈 {

    @Id
    private String id; // Elasticsearch의 문서 식별자

    private Date timestamp;

    public void generateId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String formattedTimestamp = dateFormat.format(timestamp);
        this.id = formattedTimestamp; // 혹은 다른 변환 방식을 적용하여 고유한 식별자 생성
    }

    @Field(type = FieldType.Keyword, name = "key")
    private String key;

    @Field(type = FieldType.Text, name = "name")
    private String self;

    // 특정 이슈 조회 시 사용
    private 이슈필드 fields;

    // 특정 프로젝트의 전체 이슈 조회 시 사용
    private List<지라이슈> issues;

}
