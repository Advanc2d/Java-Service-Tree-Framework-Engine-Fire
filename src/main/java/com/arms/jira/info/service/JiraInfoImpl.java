package com.arms.jira.info.service;

import com.arms.jira.info.dao.JiraInfoJpaRepository;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.model.JiraInfoEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public List<JiraInfoDTO> loadConnectInfos() {
        List<JiraInfoEntity> jiraInfoEntityList = jiraInfoJpaRepository.findAll();
        List<JiraInfoDTO> result = new ArrayList<>();
        for (JiraInfoEntity jiraInfoEntity : jiraInfoEntityList) {
            result.add(modelMapper.map(jiraInfoEntity, JiraInfoDTO.class));
        }
        return result;
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
