package com.arms.jira.elasticinfo.controller;

import com.arms.jira.elasticinfo.model.엘라스틱_지라연결정보_데이터;
import com.arms.jira.elasticinfo.model.엘라스틱_지라연결정보_엔티티;
import com.arms.jira.elasticinfo.service.엘라스틱_지라연결_서비스;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/jira/elastic")
public class 엘라스틱_지라연결_컨트롤러 {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    엘라스틱_지라연결_서비스 엘라스틱_지라연결_서비스;
    @ResponseBody
    @RequestMapping(
            value = {"/connect/info"},
            method = {RequestMethod.POST}
    )
    public 엘라스틱_지라연결정보_엔티티 setJiraConnectInfo(@RequestBody 엘라스틱_지라연결정보_데이터 엘라스틱_지라연결정보_데이터,
                                              ModelMap model, HttpServletRequest request) throws Exception {

        logger.info("Jira Connect Info SET API 호출");

        return 엘라스틱_지라연결_서비스.연결정보_저장(엘라스틱_지라연결정보_데이터);
    }
}
