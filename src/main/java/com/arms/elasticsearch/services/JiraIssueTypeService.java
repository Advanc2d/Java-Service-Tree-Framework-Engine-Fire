package com.arms.elasticsearch.services;

import java.util.List;


import com.arms.elasticsearch.models.EsJiraIssueType;

public interface JiraIssueTypeService {

	void createJiraIssueTypeIndexBulk(final List<EsJiraIssueType> esJiraIssueTypes);
}
