package com.arms.cloud.jiraissuetypescheme.controller;

import com.arms.cloud.jiraissuetypescheme.domain.CloudJiraIssueTypeSchemeMappingDTO;
import com.arms.cloud.jiraissuetypescheme.service.CloudJiraIssueTypeScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = {"/cloud/jira/issuetypescheme"})
public class CloudJiraIssueTypeSchemeController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CloudJiraIssueTypeScheme cloudJiraIssueTypeScheme;

    @ResponseBody
    @RequestMapping(
            value = {"/mapping"},
            method = {RequestMethod.GET}
    )
    public CloudJiraIssueTypeSchemeMappingDTO getIssueTypeSchemeMapping(ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("Jira Cloud ISSUE TYPE SCHEME MAPPING GET API 호출");
        return cloudJiraIssueTypeScheme.getIssueTypeSchemeMapping();
    }

    @ResponseBody
    @RequestMapping(
            value = {"/check/requirement"},
            method = {RequestMethod.PUT}
    )
    public String addIssueTypeSchemeReqIssueType(ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("Jira Cloud ISSUE TYPE SCHEME CHECK REQUIREMENT PUT API 호출");
        return cloudJiraIssueTypeScheme.addIssueTypeSchemeReqIssueType();
    }
}
