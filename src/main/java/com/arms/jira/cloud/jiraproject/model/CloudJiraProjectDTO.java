package com.arms.jira.cloud.jiraproject.model;

import java.util.List;
import java.util.Map;

import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeDTO;
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
	// non use
	//private String expand;
	private String self;
	private String id;
	private String key;
	private String name;

	// non use
	//private Map<String, Object> avatarUrls;
	// non use
	//private String projectTypeKey;
	// non use
	//private boolean simplified;
	// non use
	//private String style;

	// non use
	//private boolean isPrivate;
	// non use
	//private Map<String, Object> properties;

	// Project 개별 조회 시 추가 데이터
	// non use
	//private String description;
	// non use
	//private Map<String, Object> lead;
	// non use
	//private List<ComponentDTO>  components;

	// non use :: 그러나 프로젝트의 이슈를 조회하면서 요구사항 이슈를 관리하기 위한 용도라면 사용해도 좋습니다.
	//private List<CloudJiraIssueTypeDTO> issueTypes;
	// non use
	//private String assigneeType;
	// non use
	//private List<String> versions;
	// non use
	//private Map<String,Object> roles;
}