package com.alibaba.nacos.config.server.exception;

import com.alibaba.nacos.config.server.result.code.ResultCodeEnum;

/**
 * 返回码类.
 */
public class WebCode {

    // 返回码
    private Integer code;

    // 描述信息
    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public WebCode() {
    }

    public WebCode(ResultCodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getCodeMsg();
    }

    public WebCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
