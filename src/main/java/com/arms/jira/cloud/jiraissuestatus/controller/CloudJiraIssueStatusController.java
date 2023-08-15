package com.arms.jira.cloud.jiraissuestatus.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.arms.jira.cloud.jiraissue.service.CloudJiraIssue;
import com.arms.jira.cloud.jiraissueresolution.model.ResolutionSearchDTO;
import com.arms.jira.cloud.jiraissuestatus.model.StatusSearchDTO;
import com.arms.jira.cloud.jiraissuestatus.service.CloudJiraIssueStatus;

@RestController
@RequestMapping(value = {"/{connectId}/cloud/jira/issuestatus"})
public class CloudJiraIssueStatusController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("cloudJiraIssueStatus")
    private CloudJiraIssueStatus cloudJiraIssueStatus;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public StatusSearchDTO getStatusList(@PathVariable("connectId") Long connectId,
                                            ModelMap model, HttpServletRequest request) throws Exception {

        logger.info("Cloud Jira Issue Status List API 호출");

        StatusSearchDTO result = cloudJiraIssueStatus.getStatusList(connectId);
        return result;
    }
}
