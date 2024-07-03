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

package com.alibaba.nacos.config.server.service.config.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.config.server.exception.NacosWebException;
import com.alibaba.nacos.config.server.model.ConfigAllInfo;
import com.alibaba.nacos.config.server.model.ConfigMetadata;
import com.alibaba.nacos.config.server.result.code.ResultCodeEnum;
import com.alibaba.nacos.config.server.utils.YamlParserUtil;
import com.alibaba.nacos.config.server.utils.ZipUtils;
import com.alibaba.nacos.config.server.vo.ConfigImportDataVo;
import com.alibaba.nacos.config.server.vo.ConfigImportItemVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * import zip v1/v2 reader.
 *
 * @author ysq
 * @date 2022/7/22 10:07
 */
public class ZipConfigDataReaderV2 extends AbstractZipConfigDataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipConfigDataReaderV2.class);

    public ZipConfigDataReaderV2(String namespace, ZipUtils.UnZipResult unziped) {
        super(namespace, unziped);
    }

    @Override
    public void readMetaData() {
        ZipUtils.ZipItem metaDataItem = unziped.getMetaDataItem();
        if (metaDataItem == null) {
            throw NacosWebException.createException(ResultCodeEnum.METADATA_ILLEGAL.info("metadata is empty"));
        }

        String metaContent = metaDataItem.getItemData();

        if (StringUtils.isBlank(metaContent)) {
            throw NacosWebException.createException(ResultCodeEnum.METADATA_ILLEGAL.info("metadata is empty"));
        }
        ConfigMetadata configMetadata = YamlParserUtil.loadObject(metaContent, ConfigMetadata.class);
        if (configMetadata == null) {
            throw NacosWebException.createException(ResultCodeEnum.METADATA_ILLEGAL.info("metadata is empty"));
        }
        configDataBatchVo.setMetaDataList(configMetadata.getMetadata());
    }

    @Override
    public ConfigImportDataVo parseImportData(List<ConfigAllInfo> dbConfigList) {

        ConfigImportDataVo importDataVo = new ConfigImportDataVo();

        List<ConfigImportItemVo> configItemList = configDataBatchVo.getConfigItemList();

        //配置错误或配置有,meta 没有，记录错误，直接退出
        for (ConfigImportItemVo oneConfigItem: configItemList) {

            String itemName = oneConfigItem.getItemName();
            String group = oneConfigItem.getGroup();
            String dataId = oneConfigItem.getDataId();
            String content = oneConfigItem.getContent();

            if (StringUtils.isBlank(group) || StringUtils.isBlank(dataId)) {
                //格式错误
                ConfigImportItemVo configItem = new ConfigImportItemVo();
                configItem.setItemName(itemName);
                importDataVo.addUnrecognized(configItem);

                continue;
            }

            ConfigMetadata.ConfigExportItem metaItem = super.getMetaItem(group, dataId);

            if (metaItem == null) {
                //格式错误，直接返回
                ConfigImportItemVo configItem = new ConfigImportItemVo();
                configItem.setItemName("未在元数据中找到: " + itemName);
                importDataVo.addUnrecognized(configItem);

                continue;
            }

            ConfigAllInfo configInfo = super.createConfigInfo(oneConfigItem);

            if (metaItem != null) {
                configInfo.setType(metaItem.getType());
                configInfo.setDesc(metaItem.getDesc());
                configInfo.setAppName(metaItem.getAppName());
            }
            importDataVo.addConfigData(configInfo);
        }
        return importDataVo;
    }
}