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
