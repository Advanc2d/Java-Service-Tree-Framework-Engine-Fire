package com.arms.cloud.jiraconnectinfo.domain;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "T_ARMS_CLOUD_CONNECT_INFO")
@SelectBeforeUpdate(value=true)
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class CloudJiraConnectInfoEntity {
    @Id
    @Column(name = "id")
    @Type(type="text")
    private String id;

    //@Getter @Setter
    @Column(name = "token")
    @Type(type="text")
    private String token;

    @Column(name = "url")
    @Type(type="text")
    private String url;
}
