package com.linghang.wusthelper.exception.handler;

import com.alibaba.fastjson.JSONObject;
import com.linghang.wusthelper.exception.YJSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常处理类
 */
@ControllerAdvice
public class YJSExceptionHandler {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 处理所有不可知的异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e){
        log.error(e.getMessage(), e);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", e.getMessage() + "\n操作失败！有未处理的异常,请联系后台人员");
        return jsonObject;
    }

    /**
     * 处理爬虫异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(YJSException.class)
    public Object handleYJSException(YJSException e){
        log.error(e.getMessage(), e);
        String msg = e.getMessage();
        Integer code = e.getCode();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg",msg + "请联系后台人员");
        jsonObject.put("status", code);
        return jsonObject;
    }

}
