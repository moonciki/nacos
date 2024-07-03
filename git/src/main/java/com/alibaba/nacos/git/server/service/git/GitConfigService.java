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

import com.alibaba.nacos.config.server.vo.ConfigImportResultVo;
import com.alibaba.nacos.git.server.vo.GitCommitStatus;
import com.alibaba.nacos.git.server.vo.TenantGitVo;

/**
 * git config.
 * @author yueshiqi
 */
public interface GitConfigService {

    /**
     * 获取git配置基本信息，不包含秘钥等 .
     * @param tenantId tenantId
     * @return TenantGitVo
     */
    TenantGitVo getBaseNamespaceGit(String tenantId);

    /**
     * save git config .
     * @param tenantGitVo tenantGitVo
     * @return TenantGitVo
     */
    TenantGitVo saveNamespaceGit(TenantGitVo tenantGitVo);

    /**
     * delete git config .
     * @param tenantId tenantId
     */
    void removeNamespaceGit(String tenantId);

    /**
     * get git repo sync status.
     * @param tenantId tenantId
     * @return GitCommitVo
     */
    GitCommitStatus getCommitStatus(String tenantId);

    /**
     * sync git config.
     * @param tenantId tenantId
     * @param srcUser srcUser
     * @param srcIp srcIp
     * @param requestIpApp requestIpApp
     * @return GitSyncResultVo
     */
    ConfigImportResultVo syncConfig(String tenantId, String srcUser, String srcIp, String requestIpApp);

}