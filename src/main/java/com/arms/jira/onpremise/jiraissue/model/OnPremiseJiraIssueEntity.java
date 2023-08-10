package com.arms.jira.onpremise.jiraissue.model;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@Table(name = "ENGINE_JIRA_ISSUE")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(OnPremiseJiraIssuePK.class)
public class OnPremiseJiraIssueEntity {
    @Id
    @Column(name = "self", columnDefinition = "VARCHAR(500)")
    private String self;

    @Id
    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "connect_id", columnDefinition = "TEXT")
    private String connectId;

    @Column(name = "id", columnDefinition = "TEXT")
    private String id;

    @Column(name = "issue_key", columnDefinition = "TEXT")
    private String key;

    @Column(name = "outward_id", columnDefinition = "TEXT")
    private String outwardId;

    @Column(name = "parent_id", columnDefinition = "TEXT")
    private String parentId;

    @PrePersist // 데이터 삽입 시점의 시간 기록
    public void prePersist() {
        if (timestamp == null) {
            timestamp = new Timestamp(System.currentTimeMillis());
        }
    }
}
