package com.arms.jira.onpremise.jirapriority.controller;

import com.arms.jira.onpremise.jirapriority.service.OnPremiseJiraPriority;
import com.atlassian.jira.rest.client.api.domain.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{connectId}/onpremise/jira/priority")
public class OnPremiseJiraPriorityController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OnPremiseJiraPriority onPremiseJiraPriority;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<Priority> getPriorityList(@PathVariable("connectId") Long connectId) throws Exception {
        logger.info("우선순위 조회 API 호출");
        return onPremiseJiraPriority.getPriorityList(connectId);
    }

}
