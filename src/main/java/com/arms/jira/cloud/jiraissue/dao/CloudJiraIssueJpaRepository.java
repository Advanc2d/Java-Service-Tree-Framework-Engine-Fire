package com.arms.jira.cloud.jiraissue.dao;

import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudJiraIssueJpaRepository extends JpaRepository<CloudJiraIssueEntity, String> {
}
