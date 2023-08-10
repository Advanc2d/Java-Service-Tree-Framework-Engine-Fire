package com.arms.jira.onpremise.jiraissue.controller;

import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueInputDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueInputDTO;
import com.arms.jira.onpremise.jiraissue.service.OnPremiseJiraIssue;
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

    //조회
    @ResponseBody
    @RequestMapping(
            value = {"/list/{projectKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public OnPremiseJiraIssueDTO miningDataListToaRMS(@PathVariable("connectId") String connectId,
                                                      @PathVariable String projectKeyOrId,
                                                      ModelMap model, HttpServletRequest request) throws Exception {
        return onPremiseJiraIssue.getIssueSearch(connectId, projectKeyOrId);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public String miningDataToaRMS(@PathVariable("connectId") String connectId,
                                   @PathVariable String issueKeyOrId,
                                   ModelMap model, HttpServletRequest request) throws Exception {
        return onPremiseJiraIssue.getIssue(connectId, issueKeyOrId);
    }
    //수정
    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.PUT}
    )
    public Map<String,Object> updateIssueForReqAdd(@PathVariable("connectId") String connectId,
                                                   @PathVariable String issueKeyOrId,
                                                   @RequestBody CloudJiraIssueInputDTO cloudJiraIssueInputDTO) {
        return onPremiseJiraIssue.updateIssue(connectId, issueKeyOrId, cloudJiraIssueInputDTO);
    }
    //생성

    //삭제
}
