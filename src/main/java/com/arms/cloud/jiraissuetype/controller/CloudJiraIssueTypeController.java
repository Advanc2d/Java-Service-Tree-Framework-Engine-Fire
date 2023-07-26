package com.arms.cloud.jiraissuetype.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeDTO;
import com.arms.cloud.jiraissuetype.domain.CloudJiraIssueTypeInputDTO;
import com.arms.cloud.jiraissuetype.service.CloudJiraIssueType;

@Controller
@RequestMapping(value = {"/cloud/jira/issuetype"})
public class CloudJiraIssueTypeController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CloudJiraIssueType cloudJiraIssueType;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<CloudJiraIssueTypeDTO> getIssueTypeList(ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("Jira Cloud ALL ISSUE TYPE GET API 호출");
        return cloudJiraIssueType.getIssueTypeList();
    }

    @ResponseBody
    @RequestMapping(
            value = {""},
            method = {RequestMethod.POST}
    )
    public CloudJiraIssueTypeDTO createIssueType(@RequestBody CloudJiraIssueTypeInputDTO cloudJiraIssueTypeInputDTO,
                                                ModelMap model, HttpServletRequest request ) throws Exception {
        logger.info("Jira Cloud CREATE ISSUE TYPE POST API 호출");
        return cloudJiraIssueType.createIssueType(cloudJiraIssueTypeInputDTO);
    }
}
