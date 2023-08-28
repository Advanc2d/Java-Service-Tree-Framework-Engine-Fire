package com.arms.jira.onpremise.jiraissuetype.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.arms.jira.onpremise.jiraissuetype.model.OnPremiseJiraIssueTypeDTO;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service("onPremiseJiraIssueType")
public class OnPremiseJiraIssueTypeImpl implements OnPremiseJiraIssueType{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @Override
    public List<OnPremiseJiraIssueTypeDTO> getOnPremiseIssueTypeListAll(Long connectId) throws Exception {
        JiraInfoDTO jiraInfoDTO = 지라연결_서비스.checkInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(jiraInfoDTO.getUri(),
                                                                jiraInfoDTO.getUserId(),
                                                                jiraInfoDTO.getPasswordOrToken());

        Iterable<IssueType> issueTypes = restClient.getMetadataClient().getIssueTypes().get();
        List<OnPremiseJiraIssueTypeDTO> issueTypeList = new ArrayList<>();

        for (IssueType issueType : issueTypes) {
            OnPremiseJiraIssueTypeDTO onPremiseJiraIssueTypeDTO = new OnPremiseJiraIssueTypeDTO();

            onPremiseJiraIssueTypeDTO.setId(issueType.getId().toString());
            onPremiseJiraIssueTypeDTO.setName(issueType.getName());
            onPremiseJiraIssueTypeDTO.setSelf(issueType.getName());
            onPremiseJiraIssueTypeDTO.setSubtask(issueType.isSubtask());
            onPremiseJiraIssueTypeDTO.setDescription(issueType.getDescription());

            issueTypeList.add(onPremiseJiraIssueTypeDTO);
        }

        logger.info(issueTypeList.toString());

        return issueTypeList;
    }

    public OnPremiseJiraIssueTypeDTO getIssueTypeListByIssueTypeId(Long connectId, String issueTypeId) throws Exception {
        List<OnPremiseJiraIssueTypeDTO> issueTypes = getOnPremiseIssueTypeListAll(connectId);

        OnPremiseJiraIssueTypeDTO result = issueTypes.stream()
                .filter(it -> it.getId().equals(issueTypeId)) // 주어진 ID와 일치하는 IssueType을 찾습니다.
                .findFirst().orElse(null); // 결과가 없으면 null을 반환합니다.

        if (result != null) {
            logger.info("찾은 IssueType: " + result);
        } else {
            logger.info("ID가 " + issueTypeId + "인 IssueType을 찾을 수 없습니다.");
        }

        logger.info(result.toString());

        return result;
    }

    public Map<String, Object> checkReqIssueType(Long connectId) throws Exception {
        List<OnPremiseJiraIssueTypeDTO> issueTypes = getOnPremiseIssueTypeListAll(connectId);

        List<String> searchTerms = Arrays.asList("요구사항", "Requirement");

        List<OnPremiseJiraIssueTypeDTO> issueTypeResult = issueTypes.stream()
                .filter(it -> searchTerms.stream()
                                        .anyMatch(term -> term.equalsIgnoreCase(it.getName())))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<String, Object>();
        if (issueTypeResult.isEmpty()) {
            result.put("success" , false);
            result.put("message" , "요구사항 이슈 타입이 존재하지 않습니다.");
            result.put("issueTypeId", null);
        }
        else {
            result.put("success" , true);
            result.put("message" , "요구사항 이슈 타입이 존재합니다.");
            result.put("issueTypeId", issueTypeResult.get(0).getId());
        }

        return result;
    }

}
