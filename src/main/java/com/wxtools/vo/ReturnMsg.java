package com.wxtools.vo;

/**
 * Created by Administrator on 2017/10/16.
 */
public class ReturnMsg<T> {
    private String code;
    private String message;
    private T obj;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
