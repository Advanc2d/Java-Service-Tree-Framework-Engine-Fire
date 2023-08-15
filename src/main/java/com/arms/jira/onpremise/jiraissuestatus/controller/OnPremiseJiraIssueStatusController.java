package com.arms.jira.onpremise.jiraissuestatus.controller;


import com.arms.jira.onpremise.jiraissuestatus.model.OnPremiseJiraIssueStatusDTO;
import com.arms.jira.onpremise.jiraissuestatus.service.OnPremiseJiraIssueStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/{connectId}/onpremise/jira/issuestatus")
public class OnPremiseJiraIssueStatusController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OnPremiseJiraIssueStatus onPremiseJiraIssueStatus;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<OnPremiseJiraIssueStatusDTO> getStatusList(@PathVariable("connectId") Long connectId,
                                                           ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("이슈 상태 리스트 조회");
        return onPremiseJiraIssueStatus.getStatusList(connectId);
    }




}
