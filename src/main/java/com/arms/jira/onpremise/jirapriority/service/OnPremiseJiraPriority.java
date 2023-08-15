package com.arms.jira.onpremise.jirapriority.service;

import com.arms.jira.onpremise.jirapriority.model.OnPremiseJiraPriorityDTO;

import java.util.List;

public interface OnPremiseJiraPriority {
    List<OnPremiseJiraPriorityDTO> getPriorityList(Long connectId) throws Exception;
}
