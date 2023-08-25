package com.arms.elasticsearch.controllers;

import com.arms.elasticsearch.models.지라이슈;
import com.arms.jira.jiraissue.model.지라_이슈_데이터_전송_객체;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.arms.elasticsearch.services.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/engine/jira/{connectId}/issue")
public class 지라이슈_컨트롤러 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라이슈_서비스 지라이슈_검색엔진;

    @ResponseBody
    @RequestMapping(
            value = {"/test"},
            method = {RequestMethod.GET}
    )
    public 지라이슈 이슈_추가_테스트(@PathVariable("connectId") Long 연결_아이디,
                          ModelMap model, HttpServletRequest request) throws Exception {




        지라이슈.프로젝트 더미프로젝트 = 지라이슈.프로젝트.builder()
                .id("더미테스트프로젝트")
                .key("DUMMY-313")
                .name("프로젝트이름")
                .self("http://www.a-rms.net")
                .build();

        지라이슈 더미이슈 = 지라이슈.builder()
                .self("http://www.313.co.kr")
                .key("313devgrp")
                .project(더미프로젝트)
                .build();

        return 지라이슈_검색엔진.인덱스_추가하기(더미이슈);
    }

}
