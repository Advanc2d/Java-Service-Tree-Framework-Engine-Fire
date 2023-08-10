package com.arms.jira.onpremise.jiraissue.dao;

import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnPremiseJiraIssueJpaRepository extends JpaRepository<OnPremiseJiraIssueEntity, String> {
}
