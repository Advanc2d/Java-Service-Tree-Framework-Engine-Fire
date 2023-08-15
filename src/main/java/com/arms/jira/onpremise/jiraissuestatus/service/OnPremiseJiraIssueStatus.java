package com.arms.jira.onpremise.jiraissuestatus.service;

import com.arms.jira.onpremise.jiraissuestatus.model.OnPremiseJiraIssueStatusDTO;
import java.util.List;

public interface OnPremiseJiraIssueStatus {

    //이슈 상태리스트 조회
    List<OnPremiseJiraIssueStatusDTO> getStatusList(Long connectId) throws Exception;
}
