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

package com.alibaba.nacos.config.server.service.repository;

import com.alibaba.nacos.config.server.model.ConfigAllInfo;
import com.alibaba.nacos.config.server.model.SameConfigPolicy;
import com.alibaba.nacos.config.server.vo.ConfigBatchResultVo;
import com.alibaba.nacos.config.server.vo.ConfigImportDataVo;
import com.alibaba.nacos.config.server.vo.ConfigImportItemVo;
import com.alibaba.nacos.config.server.vo.ConfigImportResultVo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * config info service.
 *
 * @author Shiqi Yue
 */
public interface ConfigInfoHandlerService {

    /**
     * 对象转换.
     * @param configInfoList configInfoList
     * @return List ConfigImportItemVo
     */
    List<ConfigImportItemVo> toConfigItemList(List<ConfigAllInfo> configInfoList);

    /**
     * 批量保存导入配置.
     * @param configImportData configImportData
     * @param srcUser srcUser
     * @param srcIp srcIp
     * @param configAdvanceMap configAdvanceMap
     * @param time time
     * @param notify notify
     * @param policy policy
     * @return ConfigImportResultVo
     */
    ConfigImportResultVo batchInsertOrUpdate(ConfigImportDataVo configImportData, String srcUser, String srcIp,
                                             Map<String, Object> configAdvanceMap, Timestamp time,
                                             boolean notify, SameConfigPolicy policy);

    /**
     * notify config change.
     * @param configImportResultVo configImportResultVo
     * @param time time
     * @param requestIpApp requestIpApp
     * @return List ConfigAllInfo
     */
    List<ConfigAllInfo> notifyConfigChange(ConfigImportResultVo configImportResultVo, Timestamp time, String requestIpApp);

    /**
     * 通知配置更改并返回原web使用对象.
     * @param configImportResultVo configImportResultVo
     * @param time time
     * @param requestIpApp requestIpApp
     * @return ConfigBatchResultVo
     */
    ConfigBatchResultVo notifyConfigAndReturn(ConfigImportResultVo configImportResultVo, Timestamp time, String requestIpApp);

}