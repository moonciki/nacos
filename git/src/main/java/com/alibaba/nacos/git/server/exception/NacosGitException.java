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

package com.alibaba.nacos.git.server.exception;

import com.alibaba.nacos.config.server.exception.NacosWebException;
import com.alibaba.nacos.git.server.enums.GitResponseEnum;

/**
 * git exception .
 * @author ysq
 * @date 2022/6/27 14:21
 */
public class NacosGitException extends NacosWebException {

    public NacosGitException(String errorMsg) {
        super(errorMsg);
    }

    public NacosGitException(Throwable te) {
        super(te);
    }

    /**
     * 创建捕获到的异常.
     * @param message message
     * @param t t
     * @return NacosWebException
     */
    public static NacosGitException createException(String message, Throwable t) {
        NacosGitException oe = null;
        if (t instanceof NacosGitException) {
            oe = (NacosGitException) t;
        } else {
            oe = new NacosGitException(t);
            oe.initException(GitResponseEnum.git_error.info(message));
        }

        return oe;
    }

}