package com.linghang.wusthelper.service;


import com.linghang.wusthelper.common.ServerResponse;

/**
 * 研究生service
 */
public interface YjsService {

    // 小程序服务
    ServerResponse miniLogin(String stuNum, String password);

    ServerResponse getScoresByCookie(String cookie);

    ServerResponse getStudentInfoByCookie(String cookie);

    ServerResponse getPjListByCookie(String cookie);
}
