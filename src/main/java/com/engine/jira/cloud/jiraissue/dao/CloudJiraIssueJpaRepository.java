package com.engine.jira.cloud.jiraissue.dao;

import com.engine.jira.cloud.jiraissue.domain.CloudJiraIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudJiraIssueJpaRepository extends JpaRepository<CloudJiraIssueEntity, String> {
}
