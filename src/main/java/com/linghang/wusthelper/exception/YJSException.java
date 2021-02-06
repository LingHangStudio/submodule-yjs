package com.linghang.wusthelper.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常类
 *  code -1 : 教务处网络有问题
 *  code 0 : 用户账号密码不匹配
 *  code 1 : 爬虫解析异常
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YJSException extends Exception{

    private Integer code;// 异常码

    private String msg;  // 提示信息


}
