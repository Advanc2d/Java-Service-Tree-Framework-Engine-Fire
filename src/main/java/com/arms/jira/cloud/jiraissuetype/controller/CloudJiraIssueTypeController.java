package com.arms.jira.cloud.jiraissuetype.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeDTO;
import com.arms.jira.cloud.jiraissuetype.model.CloudJiraIssueTypeInputDTO;
import com.arms.jira.cloud.jiraissuetype.service.CloudJiraIssueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = {"/{connectId}/cloud/jira/issuetype"})
public class CloudJiraIssueTypeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CloudJiraIssueType cloudJiraIssueType;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<CloudJiraIssueTypeDTO> getIssueTypeList(@PathVariable("connectId") String connectId,
                                                        ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("Jira Cloud ALL ISSUE TYPE GET API 호출");
        return cloudJiraIssueType.getIssueTypeListAll(connectId);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/project"},
            method = {RequestMethod.GET}
    )
    public List<CloudJiraIssueTypeDTO> getIssueTypeList(@PathVariable("connectId") String connectId,
                                                        @RequestParam("projectId") String projectId,
                                                        ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("Jira Cloud"+ projectId +" ProjectId ISSUE TYPE GET API 호출");
        return cloudJiraIssueType.getIssueTypeListByProjectId(connectId, projectId);
    }

    @ResponseBody
    @RequestMapping(
            value = {""},
            method = {RequestMethod.POST}
    )
    public CloudJiraIssueTypeDTO createIssueType(@PathVariable("connectId") String connectId,
                                            @RequestBody CloudJiraIssueTypeInputDTO cloudJiraIssueTypeInputDTO,
                                            ModelMap model, HttpServletRequest request ) throws Exception {
        logger.info("Jira Cloud CREATE ISSUE TYPE POST API 호출");
        return cloudJiraIssueType.createIssueType(connectId, cloudJiraIssueTypeInputDTO);
    }
}
