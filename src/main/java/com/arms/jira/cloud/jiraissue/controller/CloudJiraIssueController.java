package com.arms.jira.cloud.jiraissue.controller;


import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueDTO;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueInputDTO;
import com.arms.jira.cloud.jiraissue.service.CloudJiraIssue;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueSearchDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(value = {"/{connectId}/cloud/jira/issue"})
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
    public CloudJiraIssueSearchDTO miningDataListToaRMS(@PathVariable("connectId") String connectId,
                                                        @PathVariable String projectKeyOrId,
                                                        ModelMap model, HttpServletRequest request) throws Exception {
        return cloudJiraIssue.getIssueSearch(connectId, projectKeyOrId);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public CloudJiraIssueDTO miningDataToaRMS(@PathVariable("connectId") String connectId,
                                              @PathVariable String issueKeyOrId,
                                              ModelMap model, HttpServletRequest request) throws Exception {
        return cloudJiraIssue.getIssue(connectId, issueKeyOrId);
    }

    @ResponseBody
    @RequestMapping(
            value = {""},
            method = {RequestMethod.POST}
    )
    public CloudJiraIssueDTO makeIssueForReqAdd(@PathVariable("connectId") String connectId,
                                                @RequestBody CloudJiraIssueInputDTO cloudJiraIssueInputDTO,
                                                ModelMap model, HttpServletRequest request) throws Exception {
        return cloudJiraIssue.createIssue(connectId, cloudJiraIssueInputDTO);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.PUT}
    )
    public Map<String,Object> updateIssueForReqAdd(@PathVariable("connectId") String connectId,
                                                   @PathVariable String issueKeyOrId,
                                                   @RequestBody CloudJiraIssueInputDTO cloudJiraIssueInputDTO) {
        return cloudJiraIssue.updateIssue(connectId, issueKeyOrId, cloudJiraIssueInputDTO);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.DELETE}
    )
    public Map<String,Object> deleteDataToaRMS(@PathVariable("connectId") String connectId,
                                    @PathVariable String issueKeyOrId,
                                 ModelMap model, HttpServletRequest request) throws Exception {

    	Map<String,Object> result = cloudJiraIssue.deleteIssue(connectId, issueKeyOrId);

    	return result;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/collection/scheduler"},
            method = {RequestMethod.PUT}
    )
    public Map<String,Object> collectLinkAndSubtask(@PathVariable("connectId") String connectId,
                             ModelMap model, HttpServletRequest request) throws Exception {
    	Map<String,Object> result = cloudJiraIssue.collectLinkAndSubtask(connectId);

        return result;
    }

}
