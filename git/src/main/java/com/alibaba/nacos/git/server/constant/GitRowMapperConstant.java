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

import com.alibaba.nacos.git.server.model.TenantGit;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Manager RowMapper {@link RowMapper} for database object mapping.
 *
 * @author <a href="mailto:cdmamata@126.com">ysq</a>
 */
public final class GitRowMapperConstant {

    public static final TenantGitRowMapper TENANT_GIT_ROW_MAPPER = new TenantGitRowMapper();

    private static final class TenantGitRowMapper implements RowMapper<TenantGit> {
        
        @Override
        public TenantGit mapRow(ResultSet rs, int rowNum) throws SQLException {
            TenantGit info = new TenantGit();

            info.setId(rs.getLong("id"));
            info.setTenantId(rs.getString("tenant_id"));
            info.setRepoUuid(rs.getString("repo_uuid"));
            info.setUri(rs.getString("uri"));
            info.setUserName(rs.getString("user_name"));
            info.setPassword(rs.getString("password"));
            info.setPassphrase(rs.getString("passphrase"));
            info.setPrivateKey(rs.getString("private_key"));
            info.setPath(rs.getString("path"));
            info.setBranch(rs.getString("branch"));
            info.setGmtCreate(rs.getDate("gmt_create"));
            info.setGmtModified(rs.getDate("gmt_modified"));
            info.setSyncTime(rs.getDate("sync_time"));
            info.setCommitId(rs.getString("commit_id"));
            info.setAdvanceConfigure(rs.getString("advance_configure"));
            return info;
        }
    }

}
