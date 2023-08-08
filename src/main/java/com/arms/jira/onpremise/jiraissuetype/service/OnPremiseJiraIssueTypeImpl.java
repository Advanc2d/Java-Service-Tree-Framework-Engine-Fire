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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        JiraInfoDTO jiraInfoDTO = jiraInfo.loadConnectInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(jiraInfoDTO.getUri(),
                                                                jiraInfoDTO.getUserId(),
                                                                jiraInfoDTO.getPasswordOrToken());

        List<IssueType> issueTypes = (List<IssueType>) restClient.getMetadataClient().getIssueTypes().get();

        IssueType result = null;

        for (IssueType issueType : issueTypes) {
            if (issueType.getId() == Integer.parseInt(issueTypeId)) {
                result =  issueType;
                break;
            }
        }

        logger.info(result.toString());

        return result;
    }

    public Map<String, Object> checkReqIssueType(String connectId) throws Exception {
        List<IssueType> issueTypes = getOnPremiseIssueTypeListAll(connectId);

        Map<String, Object> result = new HashMap<String, Object>();

        for (IssueType issueType : issueTypes) {
            if ("요구사항".equals(issueType.getName())) {
                result.put("success" , true);
                result.put("message" , "요구사항 이슈 타입이 존재합니다.");
                result.put("issueTypeId", issueType.getId());

                break;
            }
            else if ("requirement".equalsIgnoreCase(issueType.getName())) {
                result.put("success" , true);
                result.put("message" , "요구사항 이슈 타입이 존재합니다.");
                result.put("issueTypeId", issueType.getId());

                break;
            }
        }

        if (result.size() == 0) {
            result.put("success" , false);
            result.put("message" , "요구사항 이슈 타입이 존재하지 않습니다.");
            result.put("issueTypeId", null);
        }

        return result;
    }

}
