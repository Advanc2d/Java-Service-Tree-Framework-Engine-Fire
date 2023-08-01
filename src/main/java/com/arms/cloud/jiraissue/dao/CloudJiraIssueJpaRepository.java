package com.arms.cloud.jiraissue.dao;

import com.arms.cloud.jiraissue.domain.CloudJiraIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudJiraIssueJpaRepository extends JpaRepository<CloudJiraIssueEntity, String> {
}
