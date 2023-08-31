package com.arms.jira.elasticinfo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_class")
@JsonTypeName("com.arms.jira.elasticinfo.model.엘라스틱_지라연결정보_엔티티")
@JsonIgnoreProperties(ignoreUnknown = true)
public class 엘라스틱_지라연결정보_엔티티 {


    @Field(type = FieldType.Keyword, name = "connectId")
    private Long connectId;

    @Field(type = FieldType.Text, name = "type")
    private String type;

    @Field(type = FieldType.Text, name = "userId")
    private String userId;

    @Field(type = FieldType.Text, name = "passwordOrToken")
    private String passwordOrToken;

    @Field(type = FieldType.Text, name = "uri")
    private String uri;




}
