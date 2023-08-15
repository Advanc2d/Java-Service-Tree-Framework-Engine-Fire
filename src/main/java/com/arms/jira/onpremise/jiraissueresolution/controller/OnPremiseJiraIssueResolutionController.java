package com.arms.jira.onpremise.jiraissueresolution.controller;

import com.arms.jira.onpremise.jiraissueresolution.model.OnPremiseJiraIssueResolutionDTO;
import com.arms.jira.onpremise.jiraissueresolution.service.OnPremiseJiraIssueResolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{connectId}/onpremise/jira/issueresolution")
public class OnPremiseJiraIssueResolutionController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OnPremiseJiraIssueResolution onPremiseJiraIssueResolution;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<OnPremiseJiraIssueResolutionDTO> getResolutionList(@PathVariable("connectId") Long connectId) throws Exception {
        logger.info("해결책 조회 API 호출");
        return onPremiseJiraIssueResolution.getResolutionList(connectId);
    }

}
