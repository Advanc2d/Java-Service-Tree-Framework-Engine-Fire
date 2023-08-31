package com.arms.serverinfo.controller;
import com.arms.serverinfo.service.서버정보_서비스;
import com.arms.serverinfo.model.서버정보_데이터;
import com.arms.serverinfo.model.서버정보_엔티티;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/engine")
public class 서버정보_컨트롤러 {
    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @ResponseBody
    @RequestMapping(
            value = {"/serverinfo"},
            method = {RequestMethod.POST}
    )
    public 서버정보_엔티티 서버정보_저장(@RequestBody 서버정보_데이터 서버정보_데이터,
                            ModelMap model, HttpServletRequest request) throws Exception {

        return 서버정보_서비스.연결정보_저장(서버정보_데이터);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/delete/serverinfo"},
            method = {RequestMethod.DELETE}
    )
    public 서버정보_엔티티 서버정보_삭제(@RequestBody 서버정보_데이터 서버정보_데이터,
                            ModelMap model, HttpServletRequest request) throws Exception {

        return 서버정보_서비스.서버정보_삭제하기(서버정보_데이터);
    }
}
