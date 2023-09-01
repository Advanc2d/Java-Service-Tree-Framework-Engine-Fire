package com.arms.serverinfo.controller;
import com.arms.serverinfo.service.서버정보_서비스;
import com.arms.serverinfo.model.서버정보_데이터;
import com.arms.serverinfo.model.서버정보_엔티티;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/engine")
public class 서버정보_컨트롤러 {
    @Autowired
    private 서버정보_서비스 서버정보_서비스;
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());
    @ResponseBody
    @RequestMapping(
            value = {"/serverinfo"},
            method = {RequestMethod.POST}
    )
    public 서버정보_엔티티 서버정보_저장(@RequestBody 서버정보_데이터 서버정보_데이터,
                            ModelMap model, HttpServletRequest request) throws Exception {

        로그.info("연결정보 저장");

        return 서버정보_서비스.연결정보_저장(서버정보_데이터);
    }
    /*
    * 테스트및 관리자를 위한 구현
    * */
    @ResponseBody
    @RequestMapping(
            value = {"/serverinfo/delete"},
            method = {RequestMethod.DELETE}
    )
    public 서버정보_엔티티 서버정보_삭제(@RequestBody 서버정보_데이터 서버정보_데이터,
                            ModelMap model, HttpServletRequest request) throws Exception {

        로그.info("특정 연결정보 삭제");

        return 서버정보_서비스.서버정보_삭제하기(서버정보_데이터);
    }
    /*
     * 테스트및 관리자를 위한 구현
     * */
    @ResponseBody
    @RequestMapping(
            value = {"/serverinfo/deleteall"},
            method = {RequestMethod.DELETE}
    )
    public void 서버정보_전부삭제(ModelMap model, HttpServletRequest request) throws Exception {

        로그.info("전체 연결정보 삭제");
        서버정보_서비스.서버정보_전체_삭제하기();
    }
}
