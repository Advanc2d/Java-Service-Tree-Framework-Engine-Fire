package com.arms.cloud.jiraconnectinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arms.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoEntity;

public interface CloudJiraConnectInfoJpaRepository extends JpaRepository<CloudJiraConnectInfoEntity, String>{
}