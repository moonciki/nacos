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

package com.alibaba.nacos.git.server.service.config.impl;

import com.alibaba.nacos.common.utils.ArrayUtils;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.config.server.constant.Constants;
import com.alibaba.nacos.config.server.exception.NacosWebException;
import com.alibaba.nacos.config.server.model.ConfigAllInfo;
import com.alibaba.nacos.config.server.model.ConfigMetadata;
import com.alibaba.nacos.config.server.result.code.ResultCodeEnum;
import com.alibaba.nacos.config.server.service.config.AbstractConfigDataReader;
import com.alibaba.nacos.config.server.utils.GroupKey;
import com.alibaba.nacos.config.server.utils.YamlParserUtil;
import com.alibaba.nacos.config.server.vo.ConfigImportDataVo;
import com.alibaba.nacos.config.server.vo.ConfigImportItemVo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * import folder reader.
 *
 * @author ysq
 * @date 2022/7/22 10:07
 */
public class FolderConfigDataReader extends AbstractConfigDataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FolderConfigDataReader.class);

    protected File configFolder;

    public FolderConfigDataReader(String namespace, File configFolder) {

        this.namespace = namespace;
        this.configFolder = configFolder;

    }

    @Override
    public void readMetaData() {

        File metaFile = new File(configFolder, Constants.CONFIG_EXPORT_METADATA_NEW);

        if (!metaFile.exists() || metaFile.isDirectory()) {
            return;
        }

        String metaContent = null;
        try {
            metaContent = FileUtils.readFileToString(metaFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("read metadata error : ", e);
            throw NacosWebException.createException(ResultCodeEnum.METADATA_ILLEGAL.info("read metadata error."));
        }

        if (StringUtils.isNotBlank(metaContent)) {
            ConfigMetadata configMetadata = YamlParserUtil.loadObject(metaContent, ConfigMetadata.class);
            if (configMetadata != null) {
                configDataBatchVo.setMetaDataList(configMetadata.getMetadata());
            }
        }
    }

    private void readDataFile(File groupFile) {

        final String group = groupFile.getName();
        File[] fileArray = groupFile.listFiles();

        if (!ArrayUtils.isEmpty(fileArray)) {
            for (File dataFile : fileArray) {

                if (dataFile.isFile()) {
                    String itemName = dataFile.getAbsolutePath();

                    String dataId = dataFile.getName();

                    ConfigImportItemVo configItem = new ConfigImportItemVo();
                    configItem.setItemName(itemName);
                    configItem.setGroup(group);
                    configItem.setDataId(dataId);

                    if (StringUtils.isNotBlank(group) && StringUtils.isNotBlank(dataId)) {
                        //when group is null, needn't to get content.
                        String content = null;
                        try {
                            content = FileUtils.readFileToString(dataFile, StandardCharsets.UTF_8);
                            configItem.setContent(content);
                        } catch (IOException e) {
                            LOGGER.error("read data file error : ", e);
                        }
                    }
                    configDataBatchVo.addConfigInfo(configItem);
                }
            }
        }

    }

    @Override
    public void readConfigInfo() {

        File[] fileArray = configFolder.listFiles();

        if (!ArrayUtils.isEmpty(fileArray)) {
            for (File groupFile : fileArray) {
                if (groupFile.isDirectory()) {
                    this.readDataFile(groupFile);
                }
            }
        }
        if (CollectionUtils.isEmpty(configDataBatchVo.getConfigItemList())) {
            throw NacosWebException.createException(ResultCodeEnum.DATA_EMPTY.info());
        }
    }

    @Override
    public ConfigImportDataVo parseImportData(List<ConfigAllInfo> dbConfigList) {

        ConfigImportDataVo importDataVo = new ConfigImportDataVo();

        List<ConfigImportItemVo> configItemList = configDataBatchVo.getConfigItemList();

        //配置错误或配置有,meta 没有，记录错误，直接退出
        for (ConfigImportItemVo oneConfigItem : configItemList) {

            String itemName = oneConfigItem.getItemName();
            String group = oneConfigItem.getGroup();
            String dataId = oneConfigItem.getDataId();
            String content = oneConfigItem.getContent();

            //meta 有，配置没有，记录错误，继续下一个
            if (StringUtils.isBlank(group) || StringUtils.isBlank(dataId)) {
                //格式错误
                ConfigImportItemVo configItem = new ConfigImportItemVo();
                configItem.setItemName(itemName);
                importDataVo.addUnrecognized(configItem);

                continue;
            }

            ConfigMetadata.ConfigExportItem metaItem = super.getMetaItem(group, dataId);

            ConfigAllInfo configInfo = super.createConfigInfo(oneConfigItem);

            if (metaItem != null) {
                configInfo.setType(metaItem.getType());
                configInfo.setDesc(metaItem.getDesc());
                configInfo.setAppName(metaItem.getAppName());
            }
            importDataVo.addConfigData(configInfo);
        }

        if (CollectionUtils.isNotEmpty(dbConfigList)) {
            List<ConfigAllInfo> configDataList = importDataVo.getConfigDataList();
            Map<String, ConfigAllInfo> configDataMap = super.getConfigMap(configDataList);

            for (ConfigAllInfo dbConfig : dbConfigList) {

                String dataId = dbConfig.getDataId();
                String group = dbConfig.getGroup();

                String metaKey = GroupKey.getKey(dataId, group);

                if (!configDataMap.containsKey(metaKey)) {
                    importDataVo.addDelete(dbConfig);
                }
            }
        }

        return importDataVo;
    }

    /**
     * folder reader.
     *
     * @param namespace    namespace
     * @param configFolder configFolder
     * @return FolderConfigDataReader
     */
    public static FolderConfigDataReader getConfigReader(String namespace, File configFolder) {

        FolderConfigDataReader folderConfigDataReader = new FolderConfigDataReader(namespace, configFolder);

        return folderConfigDataReader;
    }

}
