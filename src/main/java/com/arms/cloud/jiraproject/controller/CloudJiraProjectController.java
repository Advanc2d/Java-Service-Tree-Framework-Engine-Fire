package com.arms.cloud.jiraproject.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.arms.cloud.jiraproject.domain.CloudJiraProjectDTO;
import com.arms.cloud.jiraproject.service.CloudJiraProject;

@RestController
@RequestMapping("/{connectId}/cloud/jira/project")
public class CloudJiraProjectController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CloudJiraProject cloudJiraProject;
	
    
    @ResponseBody
    @RequestMapping(
            value = {"/{projectKey}"},
            method = {RequestMethod.GET}
    )
    public CloudJiraProjectDTO getProjectData(@PathVariable String projectKey, @PathVariable("connectId") String connectId,
                                            ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("Jira Cloud PROJECT GET API 호출");
        return cloudJiraProject.getProjectData(projectKey, connectId);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<CloudJiraProjectDTO> getProjectList(@PathVariable("connectId") String connectId,
                                                ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("Jira Cloud ALL PROJECT GET API 호출");
        return cloudJiraProject.getProjectList(connectId);
    }
}
