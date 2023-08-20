package com.arms.jira.info.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import com.arms.elasticsearch.services.JiraIssueTypeService;
import com.arms.jira.info.dao.JiraInfoJpaRepository;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.model.JiraInfoEntity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class EsJiraInfoImpl implements JiraInfo {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JiraInfoJpaRepository jiraInfoJpaRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    @Qualifier("JiraIssueTypeService")
    private JiraIssueTypeService jiraIssueTypeService;

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

    /*
     *  DB에서 조회 한 후 데이터 오류 처리
     *  임시로 checkInfo 메서드 위치 시킴
     * */
    public JiraInfoDTO checkInfo(Long connectId){

        JiraInfoDTO info = loadConnectInfo(connectId);

//        if (info.getConnectId() == null) {
//            logger.info("비정상적이 정보가 조회되었습니다.");
//            throw new IllegalArgumentException("비정상적이 정보가 조회되었습니다.");
//        }

        if(info.getUserId() == null){
            logger.info("사용자 아이디 조회에 실패했습니다.");
            throw new IllegalArgumentException("사용자 아이디 조회에 실패했습니다.");
        }

        if(info.getPasswordOrToken()== null){
            logger.info("비밀 번호 및 토큰 정보 조회에 실패했습니다.");
            throw new IllegalArgumentException("비밀 번호 및 토큰 정보 조회에 실패했습니다.");
        }
        return info;
    }
}
