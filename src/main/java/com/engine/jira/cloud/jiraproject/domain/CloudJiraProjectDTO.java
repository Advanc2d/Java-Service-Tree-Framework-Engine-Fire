package com.engine.jira.cloud.jiraproject.domain;

import java.util.List;
import java.util.Map;

import com.engine.jira.cloud.jiraissuetype.domain.CloudJiraIssueTypeDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudJiraProjectDTO {

	// Project List 조회 시 데이터
	private String expand;
	private String self;
	private String id;
	private String key;
	private String name;
	private Map<String, Object> avatarUrls;
	private String projectTypeKey;
	private boolean simplified;
	private String style;
	private boolean isPrivate;
	private Map<String, Object> properties;

	// Project 개별 조회 시 추가 데이터
	private String description;
	private Map<String, Object> lead;
	private List<ComponentDTO>  components;
	private List<CloudJiraIssueTypeDTO> issueTypes;
	private String assigneeType;
	private List<String> versions;
	private Map<String,Object> roles;
}