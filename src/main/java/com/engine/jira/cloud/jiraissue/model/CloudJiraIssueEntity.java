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
@Table(name = "T_ARMS_CLOUD_JIRAISSUE")
@SelectBeforeUpdate(value=true)
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class CloudJiraIssueEntity implements Serializable {

    @Id
    @Column(name = "issue_id")
    private String id;

    @Column(name = "issue_key")
    @Type(type="text")
    private String key;

    @Column(name = "issue_url")
    @Type(type="text")
    private String self;

    //@UpdateTimestamp
    @Column(name = "Timestamp")
    private Timestamp timestamp;

    @PrePersist // 데이터 삽입 시점의 시간 기록
    public void prePersist() {
        if (timestamp == null) {
            timestamp = new Timestamp(System.currentTimeMillis());
        }
    }
}

