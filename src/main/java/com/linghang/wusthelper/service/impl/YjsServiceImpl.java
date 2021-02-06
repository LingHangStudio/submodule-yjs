package com.linghang.wusthelper.service.impl;


import com.linghang.wusthelper.common.ServerResponse;
import com.linghang.wusthelper.entity.GraduateStudent;
import com.linghang.wusthelper.exception.YJSException;
import com.linghang.wusthelper.service.YjsService;
import com.linghang.wusthelper.spider.GraduateDept;
import com.linghang.wusthelper.spider.entity.GraduateScore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 研究生service实现类
 * cookie参数均为研究生官网模拟登录后的cookie,miniLogin()方法的data值 有效时间由研究生官网的服务器确定
 * 请求失败的方法几乎一致, 自己看下就能明白, 这里不赘述
 */
@Service("YjsService")
public class YjsServiceImpl implements YjsService {



    // 服务小程序, 无token, 不调用数据库中的信息
    @Override
    public ServerResponse miniLogin(String stuNum, String password) {
        try {
            Map<String, String> login = GraduateDept.login(stuNum, password);
            String cookie = login.get("Cookie");// 获取cookie
            return new ServerResponse(70000, "", cookie);
        } catch (YJSException e) {
            if (e.getCode() == -1)
                return new ServerResponse(70006);
            else if (e.getCode() == 0)
                return new ServerResponse(70002);
            else if (e.getCode() == 1) new ServerResponse(70003);
        } catch (Exception e) {
            return new ServerResponse(70004);
        }
        return new ServerResponse(70004);
    }

    // 获取成绩
    @Override
    public ServerResponse getScoresByCookie(String cookie) {
        try {
            if (GraduateDept.checkCookie(cookie)) {
                List<GraduateScore> scores = GraduateDept.getScores(cookie);
                return new ServerResponse(70000, "", scores);
            } else {
                return new ServerResponse(70005);
            }
        } catch (YJSException e) {
            if (e.getCode() == -1)
                return new ServerResponse(70006);
            else if (e.getCode() == 0)
                return new ServerResponse(70002);
            else if (e.getCode() == 1) new ServerResponse(70003);
        } catch (Exception e) {
            return new ServerResponse(70004);
        }
        return new ServerResponse(70004);
    }

    @Override
    public ServerResponse getStudentInfoByCookie(String cookie) {

        try {
            if (GraduateDept.checkCookie(cookie)) {
                Map<String, String> stuInfo = GraduateDept.getStuInfo(cookie);
                GraduateStudent student = new GraduateStudent();
                student.setStuNum(stuInfo.get("stuNum"));
                student.setDepartment(stuInfo.get("department"));
                student.setMajor(stuInfo.get("major"));
                student.setStuName(stuInfo.get("stuName"));
                student.setStuGrade(Integer.parseInt(stuInfo.get("stuGrade")));
                student.setStuCategory(stuInfo.get("stuCategory"));
                student.setMentorName(stuInfo.get("mentorName"));
                return new ServerResponse(70000, "", student);
            } else return new ServerResponse(70005);
        } catch (YJSException e) {
            if (e.getCode() == -1)
                return new ServerResponse(70006);
            else if (e.getCode() == 0)
                return new ServerResponse(70002);
            else if (e.getCode() == 1) new ServerResponse(70003);
        } catch (Exception e) {
            return new ServerResponse(70004);
        }
        return new ServerResponse(70004);
    }

    @Override
    public ServerResponse getPjListByCookie(String cookie) {

        try {
            if (GraduateDept.checkCookie(cookie)) {
                List<Map<String, String>> list = GraduateDept.getpjList(cookie);
                return new ServerResponse(70000, "", list);
            } else return new ServerResponse(70005);
        } catch (YJSException e) {
            if (e.getCode() == -1)
                return new ServerResponse(70006);
            else if (e.getCode() == 0)
                return new ServerResponse(70002);
            else if (e.getCode() == 1) new ServerResponse(70003);
        } catch (Exception e) {
            return new ServerResponse(70004);
        }
        return new ServerResponse(70004);
    }


}
