package com.arms.jira.onpremise.jirastatus.service;

import com.arms.jira.onpremise.jirastatus.model.OnPremiseJiraStatusDTO;
import java.util.List;

public interface OnPremiseJiraStatus {

    //이슈 상태리스트 조회
    List<OnPremiseJiraStatusDTO> getStatusList(Long connectId) throws Exception;
}
