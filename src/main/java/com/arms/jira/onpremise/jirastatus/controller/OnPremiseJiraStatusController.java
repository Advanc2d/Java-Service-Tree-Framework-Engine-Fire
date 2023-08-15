package com.arms.jira.onpremise.jirastatus.controller;


import com.arms.jira.onpremise.jirastatus.service.OnPremiseJiraStatus;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/{connectId}/onpremise/jira/status")
public class OnPremiseJiraStatusController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OnPremiseJiraStatus onPremiseJiraStatus;

    @ResponseBody
    @RequestMapping(
            value = {""},
            method = {RequestMethod.GET}
    )
    public JsonNode getStatusList(@PathVariable("connectId") Long connectId,
                                ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("이슈 상태 리스트 조회");
        return onPremiseJiraStatus.getStatusList(connectId);
    }

}
