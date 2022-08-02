/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.git.server.enums;

import com.alibaba.nacos.config.server.exception.WebCode;

/**
 * web code enum.
 * @author yueshiqi
 */
public enum ResponseEnum {
    /**
     * success.
     */
    success(200, "请求成功"),

    /**
     * 参数错误.
     */
    request_error(403, "参数错误"),

    /**
     * 没有找到资源.
     */
    not_found(404, "没有找到资源"),

    /**
     * 系统异常.
     */
    sys_error(500, "系统异常"),

    /**
     * 系统繁忙.
     */
    sys_busy(502, "系统繁忙");

    /**
     * resp code.
     */
    private Integer code;

    /**
     * msg info.
     */
    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    ResponseEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * webcode .
     * @return WebCode
     */
    public WebCode info() {
        WebCode respCode = new WebCode(this.code, this.msg);
        return respCode;
    }

    /**
     * create webCode .
     * @param reqMsg reqMsg
     * @return WebCode
     */
    public WebCode info(String reqMsg) {

        WebCode respCode = new WebCode(this.code, reqMsg);

        return respCode;
    }

}
