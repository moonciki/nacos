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

import com.alibaba.nacos.config.server.model.ConfigAllInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * import config data vo.
 *
 * @author ysq
 * @date 2022/7/22 10:14
 */
public class ConfigImportDataVo {
    /**
     * 修改的数据.
     */
    private List<ConfigAllInfo> configDataList;

    /**
     * 删除的.
     */
    private List<ConfigAllInfo> deleteList;

    /**
     * 无法解析的错误.
     */
    private List<ConfigImportItemVo> unrecognizedList;

    public List<ConfigAllInfo> getConfigDataList() {
        return configDataList;
    }

    public void setConfigDataList(List<ConfigAllInfo> configDataList) {
        this.configDataList = configDataList;
    }

    public List<ConfigAllInfo> getDeleteList() {
        return deleteList;
    }

    public void setDeleteList(List<ConfigAllInfo> deleteList) {
        this.deleteList = deleteList;
    }

    public List<ConfigImportItemVo> getUnrecognizedList() {
        return unrecognizedList;
    }

    public void setUnrecognizedList(List<ConfigImportItemVo> unrecognizedList) {
        this.unrecognizedList = unrecognizedList;
    }

    /**
     * add ConfigData.
     * @param configInfo configInfo
     */
    public void addConfigData(ConfigAllInfo configInfo) {
        if (configDataList == null) {
            configDataList = new ArrayList<>();
        }
        this.configDataList.add(configInfo);
    }

    /**
     * add delete.
     * @param configInfo configInfo
     */
    public void addDelete(ConfigAllInfo configInfo) {
        if (deleteList == null) {
            deleteList = new ArrayList<>();
        }
        this.deleteList.add(configInfo);
    }

    /**
     * add unrecognized.
     * @param configItem configItem
     */
    public void addUnrecognized(ConfigImportItemVo configItem) {
        if (unrecognizedList == null) {
            unrecognizedList = new ArrayList<>();
        }
        this.unrecognizedList.add(configItem);
    }

}
