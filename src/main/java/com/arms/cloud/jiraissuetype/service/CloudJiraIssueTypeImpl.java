package com.arms.cloud.jiraissuetype.service;

import com.arms.cloud.CloudJiraUtils;
import com.arms.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoDTO;
import com.arms.cloud.jiraconnectinfo.service.CloudJiraConnectInfo;
import com.arms.cloud.jiraissuetype.dao.CloudJiraIssueTypeJpaRepository;
import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeDTO;
import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeEntity;
import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeInputDTO;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service("cloudJiraIssueType")
public class CloudJiraIssueTypeImpl implements CloudJiraIssueType {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Autowired
        private CloudJiraIssueTypeJpaRepository cloudJiraIssueTypeJpaRepository;
        @Autowired
        private ModelMapper modelMapper;
        @Autowired
        private CloudJiraConnectInfo cloudJiraConnectInfo;

        @Transactional
	@Override
	public CloudJiraIssueTypeDTO createIssueType(String connectId, 
                                                CloudJiraIssueTypeInputDTO cloudJiraIssueTypeInputDTO) 
                                                throws Exception {

                String endpoint = "/rest/api/3/issuetype";

                CloudJiraConnectInfoDTO found = cloudJiraConnectInfo.loadConnectInfo(connectId);
                WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getEmail(), found.getToken());
                CloudJiraIssueTypeDTO addCloudJirarIssueTypeDTO = CloudJiraUtils.post(webClient, endpoint, 
                                                                        cloudJiraIssueTypeInputDTO, CloudJiraIssueTypeDTO.class).block();

                CloudJiraIssueTypeEntity cloudJiraIssueTypeEntity = modelMapper.map(addCloudJirarIssueTypeDTO, CloudJiraIssueTypeEntity.class);
                cloudJiraIssueTypeJpaRepository.save(cloudJiraIssueTypeEntity);

                logger.info(addCloudJirarIssueTypeDTO.toString());

                return addCloudJirarIssueTypeDTO;
	}

        @Override
        public List<CloudJiraIssueTypeDTO> getIssueTypeListByCloud(String connectId) throws Exception {

                String endpoint = "/rest/api/3/issuetype";

                CloudJiraConnectInfoDTO found = cloudJiraConnectInfo.loadConnectInfo(connectId);
                WebClient webClient = CloudJiraUtils.createJiraWebClient(found.getUri(), found.getEmail(), found.getToken());

                List<CloudJiraIssueTypeDTO> issueTypes = CloudJiraUtils.get(webClient, endpoint, List.class).block();

                logger.info(issueTypes.toString());

                return issueTypes;
	}

        public List<CloudJiraIssueTypeDTO> getIssueTypeListByDB() {

                List<CloudJiraIssueTypeEntity> issueTypeEntities = cloudJiraIssueTypeJpaRepository.findAll();
                List<CloudJiraIssueTypeDTO> issueTypeList = new ArrayList<>();

                for (CloudJiraIssueTypeEntity item : issueTypeEntities) {
                        CloudJiraIssueTypeDTO cloudJiraIssueTypeDTO = modelMapper.map(item, CloudJiraIssueTypeDTO.class);
                        issueTypeList.add(cloudJiraIssueTypeDTO);
                }

                return issueTypeList;
        }

}
