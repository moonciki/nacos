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

package com.alibaba.nacos.config.server.vo;

import com.alibaba.nacos.config.server.model.ConfigMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * batch import file data ConfigDataContentVo .
 *
 * @author ysq
 * @date 2022/7/21 18:19
 */
public class ConfigDataBatchVo {

    /**
     * metadata content text.
     */
    private List<ConfigMetadata.ConfigExportItem> metaDataList;

    private List<ConfigImportItemVo> configItemList;

    public List<ConfigMetadata.ConfigExportItem> getMetaDataList() {
        return metaDataList;
    }

    public void setMetaDataList(List<ConfigMetadata.ConfigExportItem> metaDataList) {
        this.metaDataList = metaDataList;
    }

    public List<ConfigImportItemVo> getConfigItemList() {
        return configItemList;
    }

    public void setConfigItemList(List<ConfigImportItemVo> configItemList) {
        this.configItemList = configItemList;
    }

    /**
     * add metadata.
     * @param metaItem metaItem
     */
    public void addMetadata(ConfigMetadata.ConfigExportItem metaItem) {
        if (metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        metaDataList.add(metaItem);
    }

    /**
     * add one ConfigDataItemVo .
     * @param configItem configDataItemVo
     */
    public void addConfigInfo(ConfigImportItemVo configItem) {
        if (configItemList == null) {
            configItemList = new ArrayList<>();
        }
        configItemList.add(configItem);
    }

}
