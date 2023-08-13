package com.arms.jira.cloud.jiraissue.dao;

import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CloudJiraIssueJpaRepository extends JpaRepository<CloudJiraIssueEntity, String> {
    @Query("SELECT c FROM CloudJiraIssueEntity c WHERE c.outwardId IS NULL AND c.parentId IS NULL AND c.connectId = :connectId")
    List<CloudJiraIssueEntity> findByOutwardIdAndParentIdisNullAndConnectId(@Param("connectId")  String connectId);
}
