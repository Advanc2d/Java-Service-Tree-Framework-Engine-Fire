package com.arms.elasticsearch.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Service;

import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeDTO;

import lombok.Getter;
import lombok.Setter;

@Document(indexName = "jira_issue_type")
@Getter
@Setter
public class EsJiraIssueType {

	@Id
	private String id;

	@Field(type = FieldType.Keyword, name = "self")
	private String self;

	@Field(type = FieldType.Text, name = "description")
	private String description;

	@Field(type = FieldType.Text, name = "name")
	private String name;

	@Field(type = FieldType.Text, name = "untranslatedName")
	private String untranslatedName;

	@Field(type = FieldType.Boolean, name = "subtask")
	private Boolean subtask;

	@Field(type = FieldType.Boolean, name = "hierarchyLevel")
	private Integer hierarchyLevel;


}
