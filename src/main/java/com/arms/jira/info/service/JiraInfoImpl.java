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

        //TODO. 이렇게 테스트 코드를 넣으면 안됩니다~
        //TODO. 이미 있는지 여부 확인해서 ( 동일 URL, ID, PASS ) 있다면, 추가하지 말고 조회결과값 회신하셔요~
//        Optional<JiraInfoEntity> optionalEntity = jiraInfoJpaRepository.findById(jiraInfoDTO.getConnectId());
//        if (!optionalEntity.isPresent()) {
//            return null;
//        }
//
//        JiraInfoEntity jiraInfoEntity = optionalEntity.get();

        JiraInfoEntity 신규지라서버 = new JiraInfoEntity();
        신규지라서버.setConnectId(jiraInfoDTO.getConnectId());
        신규지라서버.setUri(jiraInfoDTO.getUri());
        신규지라서버.setUserId(jiraInfoDTO.getUserId());
        신규지라서버.setPasswordOrToken(jiraInfoDTO.getPasswordOrToken());
        return jiraInfoJpaRepository.save(신규지라서버);
    }

    public JiraInfoEntity saveIssueTypeInfo(JiraInfoEntity jiraInfoEntity) {
        return jiraInfoJpaRepository.save(jiraInfoEntity);
    }
}
