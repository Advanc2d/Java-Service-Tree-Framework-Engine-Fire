package com.arms.jira.cloud.jiraissueresolution.controller;

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

import com.arms.jira.cloud.jiraissueresolution.model.ResolutionSearchDTO;
import com.arms.jira.cloud.jiraissueresolution.service.CloudJiraIssueResolution;

@RestController
@RequestMapping(value = {"/{connectId}/cloud/jira/issueresolution"})
public class CloudJiraIssueResolutionController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("cloudJiraIssueResolution")
    private CloudJiraIssueResolution cloudJiraIssueResolution;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public ResolutionSearchDTO getResolutionList(@PathVariable("connectId") Long connectId,
                                            ModelMap model, HttpServletRequest request) throws Exception {
        ResolutionSearchDTO result = cloudJiraIssueResolution.getResolutionList(connectId);
        return result;
    }
}
