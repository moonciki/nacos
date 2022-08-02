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

package com.alibaba.nacos.git.server.dao.impl;

import com.alibaba.nacos.common.utils.UuidUtils;
import com.alibaba.nacos.git.server.constant.GitRowMapperConstant;
import com.alibaba.nacos.git.server.dao.TenantGitDao;
import com.alibaba.nacos.git.server.model.TenantGit;
import com.alibaba.nacos.persistence.configuration.condition.ConditionOnExternalStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * ExternalTenantGitImpl.
 *
 * @author ysq
 */
@Conditional(value = ConditionOnExternalStorage.class)
@Repository
public class ExternalTenantGitDaoImpl extends BaseExternalDaompl implements TenantGitDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalTenantGitDaoImpl.class);

    @Override
    public TenantGit getFullTenantGit(String tenantId) {

        String querySql = "select * from tenant_git where tenant_id = ? ";

        TenantGit tenantGit = super.queryForObject(querySql, GitRowMapperConstant.TENANT_GIT_ROW_MAPPER, tenantId);

        return tenantGit;
    }

    @Override
    public void insertNamespaceGit(TenantGit tenantGit) {

        String repoUuid = UuidUtils.generateUuid();

        Date nowTime = new Date();

        String sql = "insert into tenant_git (tenant_id, repo_uuid, uri, user_name, password, passphrase, "
                + "private_key, path, branch, gmt_create, advance_configure) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int update = jdbcTemplate.update(sql, tenantGit.getTenantId(), repoUuid, tenantGit.getUri(),
                tenantGit.getUserName(), tenantGit.getPassword(), tenantGit.getPassphrase(), tenantGit.getPrivateKey(),
                tenantGit.getPath(), tenantGit.getBranch(), nowTime, tenantGit.getAdvanceConfigure());

        LOGGER.info("insertNamespaceGit : " + tenantGit.getTenantId());

    }

    @Override
    public void updateNamespaceGit(TenantGit tenantGit) {

        Date nowTime = new Date();

        String sql = "update tenant_git set uri = ?, user_name = ?, password = ?, passphrase = ?, "
                + "private_key = ?, path = ?, branch = ?, gmt_modified = ?, advance_configure = ? "
                + "where tenant_id = ?";

        int update = jdbcTemplate.update(sql, tenantGit.getUri(), tenantGit.getUserName(), tenantGit.getPassword(),
                tenantGit.getPassphrase(), tenantGit.getPrivateKey(), tenantGit.getPath(), tenantGit.getBranch(),
                nowTime, tenantGit.getAdvanceConfigure(), tenantGit.getTenantId());

        LOGGER.info("updateNamespaceGit : " + tenantGit.getTenantId());

    }

    @Override
    public void deleteNamespaceGit(String tenantId) {

        String sql = "delete from tenant_git where tenant_id = ?";

        int update = jdbcTemplate.update(sql, tenantId);

        LOGGER.info("deleteNamespaceGit : " + tenantId);

    }

    @Override
    public void updateCommit(String tenantId, String commitId) {
        Date nowTime = new Date();

        String sql = "update tenant_git set sync_time = ?, commit_id = ? where tenant_id = ?";

        int update = jdbcTemplate.update(sql, nowTime, commitId, tenantId);

        LOGGER.info("updateCommit => tenantId : {}, commitId : {}", tenantId, commitId);

    }
}
