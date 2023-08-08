package com.arms.jira.info.controller;

import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.model.JiraInfoEntity;
import com.arms.jira.info.service.JiraInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/jira")
public class JiraInfoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JiraInfo jiraInfo;

    @ResponseBody
    @RequestMapping(
            value = {"/connect/info"},
            method = {RequestMethod.POST}
    )
    public JiraInfoEntity setJiraConnectInfo(@RequestBody JiraInfoDTO jiraInfoDTO,
                                             ModelMap model, HttpServletRequest request) throws Exception {

        logger.info("Jira Connect Info SET API 호출");

        return jiraInfo.saveConnectInfo(jiraInfoDTO);
    }
}
