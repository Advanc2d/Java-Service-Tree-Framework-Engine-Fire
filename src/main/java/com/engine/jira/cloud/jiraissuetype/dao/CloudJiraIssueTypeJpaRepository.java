package com.engine.jira.cloud.jiraissuetype.dao;

import com.engine.jira.cloud.jiraissuetype.domain.CloudJiraIssueTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudJiraIssueTypeJpaRepository extends JpaRepository<CloudJiraIssueTypeEntity, String>{
}
