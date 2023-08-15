package com.arms.elasticsearch.services.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import com.arms.elasticsearch.models.EsJiraIssueType;
import com.arms.elasticsearch.repositories.EsJiraIssueTypeRepository;
import com.arms.elasticsearch.services.JiraIssueTypeService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service("JiraIssueTypeService")
public class EsJiraIssueTypeServiceWithRepo implements JiraIssueTypeService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final EsJiraIssueTypeRepository esJiraIssueTypeRepository;

	@Override
	public void createJiraIssueTypeIndexBulk(final List<EsJiraIssueType> esJiraIssueTypes){
		logger.info("EsJiraIssueTypeServiceWithRepo");
		esJiraIssueTypeRepository.saveAll(esJiraIssueTypes);
	}

	// @Override
	// public List<EsJiraIssueType> findJi(final List<EsJiraIssueType> esJiraIssueTypes){
	// 	logger.info("EsJiraIssueTypeServiceWithRepo");
	// 	esJiraIssueTypeRepository.saveAll(esJiraIssueTypes);
	// }

}

