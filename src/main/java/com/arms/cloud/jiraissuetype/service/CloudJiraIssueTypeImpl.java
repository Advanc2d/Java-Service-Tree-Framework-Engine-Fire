package com.arms.cloud.jiraissuetype.service;

import com.arms.cloud.jiraissuetype.dao.CloudJiraIssueTypeJpaRepository;
import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeDTO;
import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeEntity;
import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeInputDTO;
import com.arms.config.CloudJiraConfig;
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
        private CloudJiraConfig cloudJiraConfig;
        @Autowired
        private CloudJiraIssueTypeJpaRepository cloudJiraIssueTypeJpaRepository;
        @Autowired
        private ModelMapper modelMapper;

        @Override
        public List<CloudJiraIssueTypeDTO> getIssueTypeListByCloud() throws Exception {
                final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

                String endpoint = "/rest/api/3/issuetype";

                List<CloudJiraIssueTypeDTO> issueTypes = jiraWebClient.get()
                        .uri(endpoint)
                        .retrieve()
                        .bodyToMono(List.class).block();

                logger.info(issueTypes.toString());

                return issueTypes;
	}
    @Transactional
	@Override
	public CloudJiraIssueTypeDTO createIssueType(CloudJiraIssueTypeInputDTO cloudJiraIssueTypeInputDTO)
			throws Exception {
                final WebClient jiraWebClient = cloudJiraConfig.getJiraWebClient();

                String endpoint = "/rest/api/3/issuetype";

                CloudJiraIssueTypeDTO addCloudJirarIssueTypeDTO = jiraWebClient.post()
                        .uri(endpoint)
                        .bodyValue(cloudJiraIssueTypeInputDTO)
                        .retrieve()
                        .bodyToMono(CloudJiraIssueTypeDTO.class).block();

                CloudJiraIssueTypeEntity cloudJiraIssueTypeEntity = modelMapper.map(addCloudJirarIssueTypeDTO, CloudJiraIssueTypeEntity.class);
                cloudJiraIssueTypeJpaRepository.save(cloudJiraIssueTypeEntity);

                logger.info(addCloudJirarIssueTypeDTO.toString());

                return addCloudJirarIssueTypeDTO;
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
