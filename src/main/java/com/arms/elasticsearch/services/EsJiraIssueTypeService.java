package com.arms.elasticsearch.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import com.arms.elasticsearch.models.EsJiraIssueType;
import com.arms.elasticsearch.models.Product;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class EsJiraIssueTypeService {
	private static final String JIRA_ISSUE_TYPE_INDEX = "jira_issue_type";
	private final ElasticsearchOperations elasticsearchOperations;

	public EsJiraIssueTypeService(ElasticsearchOperations elasticsearchOperations) {
		this.elasticsearchOperations = elasticsearchOperations;
	}

	public List<IndexedObjectInformation> createProductIndexBulk(final List<EsJiraIssueType> esJiraIssueTypes){
		List<IndexQuery> queries = esJiraIssueTypes.stream()
			.map(esJiraIssueType -> new IndexQueryBuilder().withId(String.valueOf(esJiraIssueType.getIdByUrl()))
				.withObject(esJiraIssueType).build())
			.collect(Collectors.toList());
		return elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(JIRA_ISSUE_TYPE_INDEX));
	}

}
