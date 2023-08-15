package com.arms.jira.onpremise.jirastatus.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface OnPremiseJiraStatus {

    //이슈 상태리스트 조회
    JsonNode getStatusList(Long connectId) throws Exception;
}
