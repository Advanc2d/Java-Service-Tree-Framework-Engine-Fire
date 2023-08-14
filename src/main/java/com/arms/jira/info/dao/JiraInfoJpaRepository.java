package com.arms.jira.info.dao;

import com.arms.jira.info.model.JiraInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JiraInfoJpaRepository extends JpaRepository<JiraInfoEntity, Long> {
}
