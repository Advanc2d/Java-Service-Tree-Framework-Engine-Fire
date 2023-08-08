package com.arms.jira.onpremise.jiraissuetype.service;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.JiraInfo;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private JiraInfo jiraInfo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<IssueType> getOnPremiseIssueTypeListAll(String connectId) throws Exception {
        JiraInfoDTO jiraInfoDTO = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(jiraInfoDTO.getUri(),
                                                                jiraInfoDTO.getUserId(),
                                                                jiraInfoDTO.getPasswordOrToken());

        List<IssueType> issueTypes = (List<IssueType>) restClient.getMetadataClient().getIssueTypes().get();

        logger.info(issueTypes.toString());

        return issueTypes;
    }

    public IssueType getIssueTypeListByIssueTypeId(String connectId, String issueTypeId) throws Exception {
        List<IssueType> issueTypes = getOnPremiseIssueTypeListAll(connectId);

        IssueType result = issueTypes.stream()
                .filter(it -> it.getId() == Integer.parseInt(issueTypeId)) // 주어진 ID와 일치하는 IssueType을 찾습니다.
                .findFirst().orElse(null); // 결과가 없으면 null을 반환합니다.

        if (result != null) {
            System.out.println("찾은 IssueType: " + result);
        } else {
            System.out.println("ID가 " + issueTypeId + "인 IssueType을 찾을 수 없습니다.");
        }

        logger.info(result.toString());

        return result;
    }

    public Map<String, Object> checkReqIssueType(String connectId) throws Exception {
        List<IssueType> issueTypes = getOnPremiseIssueTypeListAll(connectId);

        List<String> searchTerms = Arrays.asList("요구사항", "Requirement");

        List<IssueType> issueTypeResult = issueTypes.stream()
                .filter(it -> searchTerms.contains(it.getName()))
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
