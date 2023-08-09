package com.arms.jira.onpremise.jiraissue.controller;

import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueDTO;
import com.arms.jira.onpremise.jiraissue.model.OnPremiseJiraIssueInputDTO;
import com.arms.jira.onpremise.jiraissue.service.OnPremiseJiraIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
