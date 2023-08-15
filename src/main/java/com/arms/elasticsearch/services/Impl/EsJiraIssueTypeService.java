package com.arms.elasticsearch.services.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import com.arms.elasticsearch.models.EsJiraIssueType;
import com.arms.elasticsearch.services.JiraIssueTypeService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class EsJiraIssueTypeService implements JiraIssueTypeService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String JIRA_ISSUE_TYPE_INDEX = "jira_issue_type";
	private final ElasticsearchOperations elasticsearchOperations;

	public void createJiraIssueTypeIndexBulk(final List<EsJiraIssueType> esJiraIssueTypes){
		logger.info("EsJiraIssueTypeService");
		List<IndexQuery> queries = esJiraIssueTypes.stream()
			.map(esJiraIssueType -> new IndexQueryBuilder().withId(String.valueOf(esJiraIssueType.getIdByUrl()))
				.withObject(esJiraIssueType).build())
			.collect(Collectors.toList());
		elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(JIRA_ISSUE_TYPE_INDEX));
	}

}
