package com.engine.jira.cloud.jiraissuetype.dao;

import com.engine.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudJiraIssueTypeJpaRepository extends JpaRepository<CloudJiraIssueTypeEntity, String>{
}
