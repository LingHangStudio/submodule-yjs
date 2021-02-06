package com.linghang.wusthelper.common;

/**
 * @author origin
 * code模板
 * 为了和助手后台统一, 这里直接复制的代码
 */
public enum ResponseCode {
    //7000 
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    JWCERROR(2,"教务处错误"),
    LIBERROR(3,"图书馆错误"),


    // 教务正常 密码错误
    WRONG_PASSWORD(4, "密码错误"),
    // 教务崩溃 密码和我们的数据库匹配正确
    JWC_ERROR_WEAK_LOGIN_SUCCESS(5, ".+教务崩溃，仅本地登陆成功.+"),
    // 教务崩溃 密码和我们数据库匹配失败
    JWC_ERROR_WEAK_LOGIN_FAILURE(6, "教务崩溃，本地登录失败");

//    JWC_ERROR_

    private final int code;
    private final String desc;


    ResponseCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }
}
