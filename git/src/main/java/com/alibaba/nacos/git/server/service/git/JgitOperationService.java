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

package com.alibaba.nacos.git.server.service.git;

import com.alibaba.nacos.git.server.model.TenantGit;
import com.alibaba.nacos.git.server.vo.GitCommitVo;

import java.io.File;

/**
 * git 操作实体类.
 * @author ysq
 * @date 2022/6/16 13:36
 */
public interface JgitOperationService {

    /**
     * remove jgit repo cache .
     * @param tenantGit tenantGit
     */
    void clearRepository(TenantGit tenantGit);

    /**
     * destroy repo .
     * @param tenantGit tenantGit
     */
    void destroyRepository(TenantGit tenantGit);

    /**
     * 获取git最新操作记录.
     * @param tenantGit tenantGit
     * @return GitCommitVo
     */
    GitCommitVo getNewestCommit(TenantGit tenantGit);

    /**
     * sub git repo path .
     * @param tenantGit tenantGit
     * @return path
     */
    File getConfigFolder(TenantGit tenantGit);

}
