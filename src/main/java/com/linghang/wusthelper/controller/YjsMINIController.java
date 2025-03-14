package com.linghang.wusthelper.controller;


import com.linghang.wusthelper.common.ServerResponse;
import com.linghang.wusthelper.service.YjsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 研究生小程序controller
 * 由于需求说不要数据库, 不要token, 因此全部删除, 提取公共部分
 */
@RestController
@RequestMapping(path = "/mini_program_yjs")
public class YjsMINIController {

    @Autowired
    private YjsService yjsService;

    // 研究生登录
    @PostMapping(path = "/login")
    public ServerResponse login(@RequestBody Map<String, String> map) {
        if (map.containsKey("stuNum") && map.containsKey("password"))
            return yjsService.miniLogin(map.get("stuNum"), map.get("password"));
        else return new ServerResponse(70004, "非法请求");
    }

    // 获取学生信息
    @GetMapping(path = "/studentInfo")
    public ServerResponse getStudentInfo(@RequestHeader(value = "Cookie", required = true) String cookie) {
        return yjsService.getStudentInfoByCookie(cookie);
    }

    // 获取成绩
    @GetMapping(path = "/scores")
    public ServerResponse getScores(@RequestHeader(value = "Cookie", required = true) String cookie) {
        return yjsService.getScoresByCookie(cookie);
    }

    // 获取评教列表
    @GetMapping(path = "/pjlist")
    public ServerResponse getPjList(@RequestHeader(value = "Cookie", required = true) String cookie) {
        return yjsService.getPjListByCookie(cookie);
    }

    // TODO : 获取课表

}
