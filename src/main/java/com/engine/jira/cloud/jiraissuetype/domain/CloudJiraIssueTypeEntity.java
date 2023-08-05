package com.engine.jira.cloud.jiraissuetype.domain;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@Table(name = "T_ARMS_CLOUD_JIRAISSUETYPE")
@SelectBeforeUpdate(value=true)
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class CloudJiraIssueTypeEntity implements Serializable {
   @Id
   @Column(name = "id")
   private String id;

   //@Getter @Setter
   @Column(name = "self")
   @Type(type="text")
   private String self;

   @Column(name = "description")
   @Type(type="text")
   private String description;

   @Column(name = "iconUrl")
   @Type(type="text")
   private String iconUrl;

   @Column(name = "name")
   @Type(type="text")
   private String name;

   @Column(name = "untranslatedName")
   @Type(type="text")
   private String untranslatedName;

   @Column(name = "subtask")
   private boolean subtask;

   @Column(name = "avatarId")
   private Integer avatarId;

   @Column(name = "hierarchyLevel")
   private Integer hierarchyLevel;
}
