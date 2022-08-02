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

package com.alibaba.nacos.git.server.dao;

import com.alibaba.nacos.git.server.model.TenantGit;

/**
 * tenant_git dao.
 * @author yueshiqi
 */
public interface TenantGitDao {

    /**
     * get git full info , with privateKey.
     * @param tenantId tenantId
     * @return TenantGit
     */
    TenantGit getFullTenantGit(String tenantId);

    /**
     * insert new git config.
     * @param tenantGit tenantGitVo
     */
    void insertNamespaceGit(TenantGit tenantGit);

    /**
     * update git config.
     * @param tenantGit tenantGit
     */
    void updateNamespaceGit(TenantGit tenantGit);

    /**
     * delete git config.
     * @param tenantId tenantId
     */
    void deleteNamespaceGit(String tenantId);

    /**
     * update commitid .
     * @param tenantId tenantId
     * @param commitId commitId
     */
    void updateCommit(String tenantId, String commitId);

}