package com.engine.jira.info.model;

import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@Table(name = "ENGINE_INFO")
@SelectBeforeUpdate(value=true)
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class JiraInfoEntity {

    @Id
    @Column(name = "connect_id")
    private String connectId;

    @Column(name = "userId")
    @Type(type="text")
    private String userId;

    //@Getter @Setter
    @Column(name = "password_or_token")
    @Type(type="text")
    private String passwordOrToken;

    @Column(name = "uri")
    @Type(type="text")
    private String uri;

    @Column(name = "issue_id")
    private String issueId;

    //@Getter @Setter
    @Column(name = "self")
    @Type(type="text")
    private String self;

    @Column(name = "issue_name")
    @Type(type="text")
    private String issueName;
}
