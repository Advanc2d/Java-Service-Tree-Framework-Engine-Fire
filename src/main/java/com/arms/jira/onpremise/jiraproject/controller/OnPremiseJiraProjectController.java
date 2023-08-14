package com.arms.jira.onpremise.jiraproject.controller;

import com.arms.jira.onpremise.jiraproject.model.OnPremiseJiraProjectDTO;
import com.arms.jira.onpremise.jiraproject.service.OnPremiseJiraProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{connectId}/onpremise/jira/project")
public class OnPremiseJiraProjectController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OnPremiseJiraProject onPremiseJiraProject;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<OnPremiseJiraProjectDTO> getProjectList(@PathVariable("connectId") Long connectId) throws Exception {
        logger.info("프로젝트 전체 조회 API 호출");
        return onPremiseJiraProject.getProjectList(connectId);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{projectKey}"},
            method = {RequestMethod.GET}
    )
    public OnPremiseJiraProjectDTO getProject(@PathVariable("connectId") Long connectId,
                                              @PathVariable String projectKey) throws Exception {
        logger.info("특정 프로젝트 조회 API 호출");
        return onPremiseJiraProject.getProject(connectId, projectKey);
    }

}
