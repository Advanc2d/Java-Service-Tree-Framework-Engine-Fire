package com.arms.cloud.jiraissuetype.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeEntity;

public interface CloudJiraIssueTypeJpaRepository extends JpaRepository<CloudJiraIssueTypeEntity, String>{
}
