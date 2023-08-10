package com.arms.jira.onpremise.jiraissue.controller;

import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueInputDTO;
import com.arms.jira.onpremise.jiraissue.service.OnPremiseJiraIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/{connectId}/onpremise/jira/issue")
public class OnPremiseJiraIssueController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OnPremiseJiraIssue onPremiseJiraIssue;

    @ResponseBody
    @RequestMapping(
            value = {""},
            method = {RequestMethod.POST}
    )
    public OnPremiseJiraIssueDTO createIssue(@PathVariable("connectId") String connectId,
                                             @RequestBody OnPremiseJiraIssueInputDTO onPremiseJiraIssueInputDTO) throws Exception {
        logger.info("이슈 생성 API 호출");
        return onPremiseJiraIssue.createIssue(connectId, onPremiseJiraIssueInputDTO);
    }

    // 전체 조회
    @ResponseBody
    @RequestMapping(
            value = {"/list/{projectKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public JsonNode getIssueList(@PathVariable("connectId") String connectId,
                                 @PathVariable String projectKeyOrId,
                                 ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("이슈 전체 조회 API 호출");
        return onPremiseJiraIssue.getIssueSearch(connectId, projectKeyOrId);
    }
    // 상세 조회
    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public Issue getIssueDetail(@PathVariable("connectId") String connectId,
                                  @PathVariable String issueKeyOrId,
                                  ModelMap model, HttpServletRequest request) throws Exception {
        logger.info("이슈 상세 조회 API 호출");
        return onPremiseJiraIssue.getIssue(connectId, issueKeyOrId);
    }
    //업데이트
    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.PUT}
    )
    public Map<String,Object> updateIssueForReqAdd(@PathVariable("connectId") String connectId,
                                                   @PathVariable String issueKeyOrId,
                                                   @RequestBody  OnPremiseJiraIssueInputDTO onPremiseJiraIssueInputDTO) throws Exception{
        logger.info("이슈 업데이트 API 호출");
        return onPremiseJiraIssue.updateIssue(connectId, issueKeyOrId, onPremiseJiraIssueInputDTO);
    }

    // 이슈 삭제
    @ResponseBody
    @RequestMapping(
            value = {"/{issueKey}"},
            method = {RequestMethod.DELETE}
    )
    public Map<String, Object> deleteIssue(@PathVariable("connectId") String connectId,
                                           @PathVariable String issueKey) throws Exception {
        logger.info("이슈 삭제 API 호출");
        return onPremiseJiraIssue.deleteIssue(connectId, issueKey);
    }

}
