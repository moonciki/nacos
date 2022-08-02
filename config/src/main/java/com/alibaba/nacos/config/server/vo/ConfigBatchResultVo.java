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

import java.util.List;

/**
 * 原版导入返回类，替换原来的map.
 *
 * @author ysq
 * @date 2022/7/22 14:44
 */
public class ConfigBatchResultVo {

    private Integer succCount = 0;

    private List<ConfigImportItemVo> succData;

    /**
     * 未识别的配置.
     */
    private Integer unrecognizedCount = 0;

    private List<ConfigImportItemVo> unrecognizedData;

    private Integer skipCount = 0;

    private List<ConfigImportItemVo> skipData;

    private List<ConfigImportItemVo> failData;

    public Integer getSuccCount() {
        return succCount;
    }

    public void setSuccCount(Integer succCount) {
        this.succCount = succCount;
    }

    public List<ConfigImportItemVo> getSuccData() {
        return succData;
    }

    public void setSuccData(List<ConfigImportItemVo> succData) {
        this.succData = succData;
    }

    public Integer getUnrecognizedCount() {
        return unrecognizedCount;
    }

    public void setUnrecognizedCount(Integer unrecognizedCount) {
        this.unrecognizedCount = unrecognizedCount;
    }

    public List<ConfigImportItemVo> getUnrecognizedData() {
        return unrecognizedData;
    }

    public void setUnrecognizedData(List<ConfigImportItemVo> unrecognizedData) {
        this.unrecognizedData = unrecognizedData;
    }

    public Integer getSkipCount() {
        return skipCount;
    }

    public void setSkipCount(Integer skipCount) {
        this.skipCount = skipCount;
    }

    public List<ConfigImportItemVo> getSkipData() {
        return skipData;
    }

    public void setSkipData(List<ConfigImportItemVo> skipData) {
        this.skipData = skipData;
    }

    public List<ConfigImportItemVo> getFailData() {
        return failData;
    }

    public void setFailData(List<ConfigImportItemVo> failData) {
        this.failData = failData;
    }
}
