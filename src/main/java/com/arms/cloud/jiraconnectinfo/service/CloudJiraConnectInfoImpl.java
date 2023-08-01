package com.arms.cloud.jiraconnectinfo.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.arms.cloud.jiraconnectinfo.dao.CloudJiraConnectInfoJpaRepository;
import com.arms.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoDTO;
import com.arms.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoEntity;

@Service("cloudJiraConnectInfo")
public class CloudJiraConnectInfoImpl implements CloudJiraConnectInfo {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CloudJiraConnectInfoJpaRepository cloudJiraConnectInfoJpaRepository;

    @Override
    public CloudJiraConnectInfoDTO loadConnectInfo(String id) {

        Optional<CloudJiraConnectInfoEntity> optionalEntity = cloudJiraConnectInfoJpaRepository.findById(id);
        if (!optionalEntity.isPresent()) {
            return null;
        }

        CloudJiraConnectInfoEntity cloudJiraConnectInfoEntity = optionalEntity.get();
        CloudJiraConnectInfoDTO cloudJiraConnectInfoDTO = modelMapper.map(cloudJiraConnectInfoEntity, CloudJiraConnectInfoDTO.class);

        return cloudJiraConnectInfoDTO;
    }
}
