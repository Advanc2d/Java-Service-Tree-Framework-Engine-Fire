package com.arms.elasticsearch.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.arms.elasticsearch.models.EsJiraIssueType;
import com.arms.elasticsearch.models.Product;

@Repository
public interface EsJiraIssueTypeRepository extends ElasticsearchRepository<EsJiraIssueType, String> {
}
