package com.arms.cloud.jiraissue.controller;


import com.arms.cloud.jiraissue.domain.CloudJiraIssueDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueInputDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueSearchDTO;
import com.arms.cloud.jiraissue.service.CloudJiraIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = {"/cloud/jira/issue"})
public class CloudJiraIssueController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("cloudJiraIssue")
    private CloudJiraIssue cloudJiraIssue;

    @ResponseBody
    @RequestMapping(
            value = {"/list/{projectKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public CloudJiraIssueSearchDTO miningDataListToaRMS(@PathVariable String projectKeyOrId, ModelMap model, 
                                    HttpServletRequest request) throws Exception {
        return cloudJiraIssue.getIssueSearch(projectKeyOrId);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public CloudJiraIssueDTO miningDataToaRMS(@PathVariable String issueKeyOrId, ModelMap model,
                                    HttpServletRequest request) throws Exception {
        return cloudJiraIssue.getIssue(issueKeyOrId);
    }

    @ResponseBody
    @RequestMapping(
            value = {""},
            method = {RequestMethod.POST}
    )
    public CloudJiraIssueDTO makeIssueForReqAdd(@RequestBody CloudJiraIssueInputDTO cloudJiraIssueInputDTO,
                                                ModelMap model, HttpServletRequest request) throws Exception {
        return cloudJiraIssue.createIssue(cloudJiraIssueInputDTO);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.PUT}
    )
    public String updateIssueForReqAdd(@PathVariable String issueKeyOrId,
                                       @RequestBody CloudJiraIssueInputDTO cloudJiraIssueInputDTO) {
        return cloudJiraIssue.updateIssue(issueKeyOrId, cloudJiraIssueInputDTO);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/delete/{issueKeyOrId}"},
            method = {RequestMethod.DELETE,RequestMethod.GET}
    )
    public void deleteDataToaRMS(@PathVariable String issueKeyOrId, ModelMap model,
                                              HttpServletRequest request) throws Exception {
        //return cloudJiraIssue.deleteIssue(issueKeyOrId);
    }
}
