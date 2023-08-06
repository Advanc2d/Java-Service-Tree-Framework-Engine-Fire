package com.engine.jira.info.service;

import com.engine.jira.info.dao.JiraInfoJpaRepository;
import com.engine.jira.info.model.JiraInfoDTO;
import com.engine.jira.info.model.JiraInfoEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service("jiraInfo")
public class JiraInfoImpl implements JiraInfo {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JiraInfoJpaRepository jiraInfoJpaRepository;

    public JiraInfoDTO loadConnectInfo(String connectId) {
        Optional<JiraInfoEntity> optionalEntity = jiraInfoJpaRepository.findById(connectId);

        if (!optionalEntity.isPresent()) {
            return null;
        }

        JiraInfoEntity jiraInfoEntity = optionalEntity.get();

        JiraInfoDTO jiraInfoDTO = modelMapper.map(jiraInfoEntity, JiraInfoDTO.class);

        return jiraInfoDTO;
    }

    public String getIssueTypeId(String connectId) {

        Optional<JiraInfoEntity> optionalEntity = jiraInfoJpaRepository.findById(connectId);

        if (!optionalEntity.isPresent()) {
            return null;
        }

        JiraInfoEntity jiraInfoEntity = optionalEntity.get();

        return jiraInfoEntity.getIssueId();
    }

    public JiraInfoEntity saveConnectInfo(JiraInfoDTO jiraInfoDTO) {

        Optional<JiraInfoEntity> optionalEntity = jiraInfoJpaRepository.findById(jiraInfoDTO.getConnectId());
        if (!optionalEntity.isPresent()) {
            return null;
        }

        JiraInfoEntity jiraInfoEntity = optionalEntity.get();

        return jiraInfoJpaRepository.save(jiraInfoEntity);
    }

    public JiraInfoEntity saveIssueTypeInfo(JiraInfoEntity jiraInfoEntity) {
        return jiraInfoJpaRepository.save(jiraInfoEntity);
    }
}
