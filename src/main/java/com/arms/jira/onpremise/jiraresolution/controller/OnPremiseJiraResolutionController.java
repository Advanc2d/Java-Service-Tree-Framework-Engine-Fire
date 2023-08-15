package com.arms.jira.onpremise.jiraresolution.controller;

import com.arms.jira.onpremise.jiraresolution.model.OnPremiseJiraResolutionDTO;
import com.arms.jira.onpremise.jiraresolution.service.OnPremiseJiraResolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{connectId}/onpremise/jira/resolution")
public class OnPremiseJiraResolutionController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OnPremiseJiraResolution onPremiseJiraResolution;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<OnPremiseJiraResolutionDTO> getResolutionList(@PathVariable("connectId") Long connectId) throws Exception {
        logger.info("해결책 조회 API 호출");
        return onPremiseJiraResolution.getResolutionList(connectId);
    }

}
