package com.arms.jira.onpremise.jiraissue.dao;

import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OnPremiseJiraIssueJpaRepository extends JpaRepository<OnPremiseJiraIssueEntity, String> {
    @Query("SELECT c FROM OnPremiseJiraIssueEntity c\n" +
            "WHERE c.outwardId IS NULL\n" +
            "  AND c.parentId IS NULL\n" +
            "  AND c.connectId = :connectId\n" +
            "  AND c.timestamp = (\n" +
            "    SELECT MAX(a.timestamp)\n" +
            "    FROM OnPremiseJiraIssueEntity a\n" +
            "    WHERE a.self = c.self\n" +
            "      AND a.outwardId IS NULL\n" +
            "      AND a.parentId IS NULL\n" +
            "      AND a.connectId = :connectId\n" +
            "  )")
    List<OnPremiseJiraIssueEntity> findByOutwardIdAndParentIdisNullAndConnectId(@Param("connectId")  Long connectId);
}
