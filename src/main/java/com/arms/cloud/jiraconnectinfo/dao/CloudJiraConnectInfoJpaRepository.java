package com.arms.cloud.jiraconnectinfo.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arms.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoEntity;

public interface CloudJiraConnectInfoJpaRepository extends JpaRepository<CloudJiraConnectInfoEntity, String>{
    // Optional<CloudJiraConnectInfoEntity> findByType(String type);
}