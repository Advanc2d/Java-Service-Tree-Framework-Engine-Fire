package com.arms.jira.onpremise.jiraissuetype.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OnPremiseJiraIssueTypeDTO {
    private String self;
	private String id;
	private String description;

	private String name;
	private Boolean subtask;
}