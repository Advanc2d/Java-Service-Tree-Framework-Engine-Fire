package com.arms.jira.info.service;

import com.arms.jira.info.dao.JiraInfoJpaRepository;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.model.JiraInfoEntity;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    public JiraInfoDTO loadConnectInfo(Long connectId) {
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

    public String getIssueTypeId(Long connectId) {

        Optional<JiraInfoEntity> optionalEntity = jiraInfoJpaRepository.findById(connectId);

        if (!optionalEntity.isPresent()) {
            return null;
        }

        JiraInfoEntity jiraInfoEntity = optionalEntity.get();

        return jiraInfoEntity.getIssueId();
    }

    public JiraInfoEntity saveConnectInfo(JiraInfoDTO jiraInfoDTO) {

        if (jiraInfoDTO == null) {
            throw new IllegalArgumentException("JiraInfo 정보가 없습니다.");
        }
        else if (StringUtils.isBlank(jiraInfoDTO.getUri())) {
            throw new IllegalArgumentException("JiraInfo의 URI 정보가 없습니다.");
        }
        else if (StringUtils.isBlank(jiraInfoDTO.getUserId())) {
            throw new IllegalArgumentException("JiraInfo의 사용자 아이디 정보가 없습니다.");
        }
        else if (StringUtils.isBlank(jiraInfoDTO.getPasswordOrToken())) {
            throw new IllegalArgumentException("JiraInfo의 비밀번호나 토큰 정보가 없습니다.");
        }

        JiraInfoDTO loadJiraInfoDTO = loadConnectInfo(jiraInfoDTO.getConnectId());
        JiraInfoEntity jiraInfoEntity;

        if (loadJiraInfoDTO != null) {
            jiraInfoEntity = modelMapper.map(loadJiraInfoDTO, JiraInfoEntity.class);
        }
        else {
            jiraInfoEntity = modelMapper.map(jiraInfoDTO, JiraInfoEntity.class);
        }

        return jiraInfoJpaRepository.save(jiraInfoEntity);
    }

    public JiraInfoEntity saveIssueTypeInfo(JiraInfoEntity jiraInfoEntity) {
        return jiraInfoJpaRepository.save(jiraInfoEntity);
    }
}
