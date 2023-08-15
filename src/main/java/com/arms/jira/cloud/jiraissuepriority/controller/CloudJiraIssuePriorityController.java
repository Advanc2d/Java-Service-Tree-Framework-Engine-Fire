package com.arms.jira.cloud.jiraissuepriority.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.arms.jira.cloud.jiraissuepriority.model.PrioritySearchDTO;
import com.arms.jira.cloud.jiraissuepriority.service.CloudJiraIssuePriority;

@RestController
@RequestMapping(value = {"/{connectId}/cloud/jira/issuepriority"})
public class CloudJiraIssuePriorityController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("cloudJiraIssuePriority")
    private CloudJiraIssuePriority cloudJiraIssuePriority;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public PrioritySearchDTO getPriorityList(@PathVariable("connectId") Long connectId,
                                            ModelMap model, HttpServletRequest request) throws Exception {
        PrioritySearchDTO result = cloudJiraIssuePriority.getPriorityList(connectId);

        return result;
    }
}
