package com.arms.jira.cloud.jiraproject.controller;

import com.arms.jira.cloud.jiraproject.model.CloudJiraProjectDTO;
import com.arms.jira.cloud.jiraproject.service.CloudJiraProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/{connectId}/cloud/jira/project")
public class CloudJiraProjectController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CloudJiraProject cloudJiraProject;

    @ResponseBody
    @RequestMapping(
            value = {"/{projectKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public CloudJiraProjectDTO getProjectData(@PathVariable String projectKeyOrId,
                                              @PathVariable("connectId") Long connectId,
                                              ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("Jira Cloud PROJECT GET API 호출");
        return cloudJiraProject.getProjectData(connectId, projectKeyOrId);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<CloudJiraProjectDTO> getProjectList(@PathVariable("connectId") Long connectId,
                                                    ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("Jira Cloud ALL PROJECT GET API 호출");
        return cloudJiraProject.getProjectList(connectId);
    }
}
