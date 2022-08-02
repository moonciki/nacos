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

package com.alibaba.nacos.git.server.constant;

import com.alibaba.nacos.sys.env.EnvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * nacos git config constant .
 * @author ysq
 * @date 2022/6/22 16:25
 */
public class NacosGitConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosGitConstant.class);

    static final String REPO_DIR_NAME = "git-config-repo";

    /**
     * git config repo dir .
     */
    public static final String GIT_CONFIG_REPO_DIR;

    static {
        File file = new File(EnvUtil.getNacosHome(), REPO_DIR_NAME);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
        }
        GIT_CONFIG_REPO_DIR = file.getAbsolutePath();
        LOGGER.info("GIT_CONFIG_REPO_DIR is : " + GIT_CONFIG_REPO_DIR);
    }

}
