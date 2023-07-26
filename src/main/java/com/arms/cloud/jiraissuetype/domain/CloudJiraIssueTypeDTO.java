package com.arms.cloud.jiraissuetype.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudJiraIssueTypeDTO {
	private String self;
	private String id;
	private String description;
	private String iconUrl;
	private String name;
	private String untranslatedName;
	private Boolean subtask;
	private Integer avatarId;
	private Integer hierarchyLevel;
}
