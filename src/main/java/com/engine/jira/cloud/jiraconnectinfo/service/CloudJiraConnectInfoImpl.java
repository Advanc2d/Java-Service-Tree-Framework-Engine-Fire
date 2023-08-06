//package com.engine.jira.cloud.jiraconnectinfo.service;
//
//import java.util.Optional;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import com.engine.jira.cloud.jiraconnectinfo.dao.CloudJiraConnectInfoJpaRepository;
//import com.engine.jira.cloud.jiraconnectinfo.model.CloudJiraConnectInfoDTO;
//import com.engine.jira.cloud.jiraconnectinfo.model.CloudJiraConnectInfoEntity;
//
//@Service("cloudJiraConnectInfo")
//public class CloudJiraConnectInfoImpl implements CloudJiraConnectInfo {
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Autowired
//    private CloudJiraConnectInfoJpaRepository cloudJiraConnectInfoJpaRepository;
//
//    @Override
//    public CloudJiraConnectInfoDTO loadConnectInfo(String connectId) {
//
//        Optional<CloudJiraConnectInfoEntity> optionalEntity = cloudJiraConnectInfoJpaRepository.findById(connectId);
//
//        if (!optionalEntity.isPresent()) {
//            return null;
//        }
//
//        CloudJiraConnectInfoEntity cloudJiraConnectInfoEntity = optionalEntity.get();
//        CloudJiraConnectInfoDTO cloudJiraConnectInfoDTO
//                                        = modelMapper.map(cloudJiraConnectInfoEntity, CloudJiraConnectInfoDTO.class);
//
//        return cloudJiraConnectInfoDTO;
//    }
//}
