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

import com.alibaba.nacos.persistence.datasource.DataSourceService;
import com.alibaba.nacos.persistence.datasource.DynamicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;

/**
 * 数据库基础类.
 * @author ysq
 * @date 2022/6/16 9:45
 */
public class BaseExternalDaompl {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseExternalDaompl.class);

    protected JdbcTemplate jdbcTemplate;

    protected DataSourceService dataSourceService;

    @PostConstruct
    public void init() {
        this.dataSourceService = DynamicDataSource.getInstance().getDataSource();
        this.jdbcTemplate = dataSourceService.getJdbcTemplate();
    }

    /**
     * 可以返回空的查询.
     * @param sql sql
     * @param rowMapper rowMapper
     * @param args args
     * @return entity
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, @Nullable Object... args) {
        T objResult = null;
        try {
            objResult = jdbcTemplate.queryForObject(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.warn("query_result_is_null ... ");
        }
        return objResult;
    }

}
