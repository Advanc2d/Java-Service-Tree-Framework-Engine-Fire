package com.arms.jira.onpremise.jiraissuetype.controller;

import com.arms.jira.onpremise.jiraissuetype.service.OnPremiseJiraIssueType;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = {"/{connectId}/onpremise/jira/issuetype"})
public class OnPremiseJiraIssueTypeController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OnPremiseJiraIssueType onPremiseJiraIssueType;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<IssueType> getIssueTypeList(@PathVariable("connectId") String connectId,
                                                  ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("Jira OnPremise ALL ISSUE TYPE GET API 호출");
        return onPremiseJiraIssueType.getOnPremiseIssueTypeListAll(connectId);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueTypeId}"},
            method = {RequestMethod.GET}
    )
    public IssueType getIssueTypeListByIssueTypeId(@PathVariable("connectId") String connectId,
                                                        @PathVariable("issueTypeId") String issueTypeId,
                                                        ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("Jira OnPremise "+ issueTypeId +" issueTypeId ISSUE TYPE GET API 호출");
        return onPremiseJiraIssueType.getIssueTypeListByIssueTypeId(connectId, issueTypeId);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/check/requirement"},
            method = {RequestMethod.GET}
    )
    public Map<String, Object> checkReqIssueType(@PathVariable("connectId") String connectId,
                                                 ModelMap model, HttpServletRequest request ) throws Exception {
        logger.info("Jira OnPremise Requirement ISSUE TYPE Check API 호출");
        return onPremiseJiraIssueType.checkReqIssueType(connectId);
    }

}
