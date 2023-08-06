package com.engine.jira.info.dao;

import com.engine.jira.info.model.JiraInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JiraInfoJpaRepository extends JpaRepository<JiraInfoEntity, String> {
}
