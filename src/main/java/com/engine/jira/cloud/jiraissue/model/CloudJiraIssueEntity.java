package com.engine.jira.cloud.jiraissue.model;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@Table(name = "ENGINE_JIRA_ISSUE")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CloudJiraIssuePK.class)
public class CloudJiraIssueEntity {

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

    //@UpdateTimestamp
    @PrePersist // 데이터 삽입 시점의 시간 기록
    public void prePersist() {
        if (timestamp == null) {
            timestamp = new Timestamp(System.currentTimeMillis());
        }
    }
}

