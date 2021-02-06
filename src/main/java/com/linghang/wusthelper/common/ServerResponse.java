package com.linghang.wusthelper.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @param <T>
 * 简化了get/set方法, 为了兼容小程序版和助手后台版
 * 返回码参考附录
 * 返回数据序列化
 */
@JsonSerialize
@Data
@NoArgsConstructor
public class ServerResponse<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int status;

    @JsonInclude(JsonInclude.Include.NON_NULL)  //  为空时省略该属性
    private String msg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ServerResponse(int status, String msg) {
        this.msg = msg;
        this.status = status;
    }

    // 当msg不为空, 设置msg
    public ServerResponse(int status, String msg, T data) {
        if (msg != null && !msg.equals(""))
            this.msg = msg;
        this.status = status;
        this.data = data;
    }

    public ServerResponse(int status) {
        this.status = status;
    }

    //  不在序列化当中
    @JsonIgnore
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public static <T> ServerResponse<T> createBySuccess() {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMsg(String msg) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode());
    }

    public static <T> ServerResponse<T> createByErrorMsg(String msg) {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), msg);
    }

    public static <T> ServerResponse<T> createByError(String msg, T data) {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> createByJWCError(T data) {
        return new ServerResponse<T>(ResponseCode.JWCERROR.getCode(), ResponseCode.JWCERROR.getDesc(), data);
    }

    public static <T> ServerResponse<T> createByJWCError() {
        return new ServerResponse<T>(ResponseCode.JWCERROR.getCode(), ResponseCode.JWCERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByLIBError() {
        return new ServerResponse<T>(ResponseCode.LIBERROR.getCode(), ResponseCode.LIBERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByLIBError(T data) {
        return new ServerResponse<T>(ResponseCode.LIBERROR.getCode(), ResponseCode.LIBERROR.getDesc(), data);
    }

    public static <T> ServerResponse<T> createByError(T data) {
        return new ServerResponse<T>(ResponseCode.LIBERROR.getCode(), ResponseCode.LIBERROR.getDesc(), data);
    }

    public static <T> ServerResponse<T> createByError(Integer code, String msg) {
        return new ServerResponse<T>(code, msg, null);
    }
}
