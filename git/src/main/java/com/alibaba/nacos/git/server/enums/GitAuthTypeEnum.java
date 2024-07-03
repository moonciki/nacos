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

/**
 * mq 处理状态.
 * @author yueshiqi
 *
 * @date 2023-11-3 17:26:36
 */
public enum GitAuthTypeEnum {
    /**
     * 用户名密码登录.
     */
    password(1, "用户名密码"),
    /**
     * 私钥登录.
     */
    privateKey(2, "私钥");

    private final Integer code;
    private final String zhName;

    public Integer getCode() {
        return code;
    }

    public String getZhName() {
        return zhName;
    }

    GitAuthTypeEnum(Integer code, String zhName) {
        this.code = code;
        this.zhName = zhName;
    }

    public static GitAuthTypeEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (GitAuthTypeEnum typeEnum : GitAuthTypeEnum.values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * code 校验.
     *
     * @param reqCode reqCode
     * @return boolean
     */
    public boolean codeEquals(Integer reqCode) {
        if (reqCode == null) {
            return false;
        }

        boolean result = this.getCode().equals(reqCode);
        return result;
    }

}
