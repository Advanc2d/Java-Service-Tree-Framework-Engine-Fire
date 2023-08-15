package com.arms.jira.onpremise.jiraissuepriority.controller;

import com.arms.jira.onpremise.jiraissuepriority.model.OnPremiseJiraIssuePriorityDTO;
import com.arms.jira.onpremise.jiraissuepriority.service.OnPremiseJiraIssuePriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{connectId}/onpremise/jira/issuepriority")
public class OnPremiseJiraIssuePriorityController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OnPremiseJiraIssuePriority onPremiseJiraIssuePriority;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<OnPremiseJiraIssuePriorityDTO> getPriorityList(@PathVariable("connectId") Long connectId) throws Exception {
        logger.info("우선순위 조회 API 호출");
        return onPremiseJiraIssuePriority.getPriorityList(connectId);
    }

}
