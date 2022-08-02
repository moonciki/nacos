package com.alibaba.nacos.config.server.service.config.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.config.server.exception.NacosWebException;
import com.alibaba.nacos.config.server.model.ConfigAllInfo;
import com.alibaba.nacos.config.server.model.ConfigMetadata;
import com.alibaba.nacos.config.server.result.code.ResultCodeEnum;
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
public class ZipConfigDataReaderV1 extends ZipConfigDataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipConfigDataReaderV1.class);

    public ZipConfigDataReaderV1(String namespace, ZipUtils.UnZipResult unziped) {
        super(namespace, unziped);
    }

    /**
     * V1 just set the appName.
     */
    @Override
    public void readMetaData() {

        ZipUtils.ZipItem metaDataZipItem = unziped.getMetaDataItem();

        if (metaDataZipItem == null) {
            return;
        }

        String metaContent = metaDataZipItem.getItemData();

        if (StringUtils.isBlank(metaContent)) {
            return;
        }

        // compatible all file separator
        String metaDataStr = metaContent.replaceAll("[\r\n]+", "|");
        String[] metaDataArr = metaDataStr.split("\\|");

        for (String metaDataItem : metaDataArr) {
            String[] metaDataItemArr = metaDataItem.split("=");
            if (metaDataItemArr.length != 2) {
                throw NacosWebException.createException(ResultCodeEnum.METADATA_ILLEGAL.info("meta error : " + metaDataItem));
            }

            final String groupDataName = metaDataItemArr[0];
            final String appName = metaDataItemArr[1];

            String[] metaDataKeyArray = groupDataName.split("\\.");

            if (metaDataKeyArray.length != 3) {
                //group    dataId    app
                throw NacosWebException.createException(ResultCodeEnum.METADATA_ILLEGAL.info("meta error : " + metaDataItem));
            }
            //!!!!!如果 group 包含“.”，这里会有问题
            String metaGroup = metaDataKeyArray[0];
            String metaDataId = metaDataKeyArray[1];

            if (StringUtils.isBlank(metaGroup) || StringUtils.isBlank(metaDataId)) {
                throw NacosWebException.createException(ResultCodeEnum.METADATA_ILLEGAL.info("meta error : " + metaDataItem));
            }

            String group = metaGroup.replaceAll("~", ".");
            String dataId = metaDataId.replaceAll("~", ".");

            ConfigMetadata.ConfigExportItem metaItem = new ConfigMetadata.ConfigExportItem();

            metaItem.setGroup(group);
            metaItem.setDataId(dataId);
            metaItem.setAppName(appName);

            configDataBatchVo.addMetadata(metaItem);
        }
    }

    @Override
    public ConfigImportDataVo parseImportData(List<ConfigAllInfo> dbConfigList) {
        //v1 不做删除操作，不校验是否改动，全都放newList
        //Map<String, ConfigAllInfo> dbConfigMap = getDbConfigMap(dbConfigList);

        ConfigImportDataVo importDataVo = new ConfigImportDataVo();

        List<ConfigImportItemVo> configItemList = configDataBatchVo.getConfigItemList();

        for (ConfigImportItemVo oneConfigItem: configItemList) {

            String itemName = oneConfigItem.getItemName();
            String group = oneConfigItem.getGroup();
            String dataId = oneConfigItem.getDataId();

            if (StringUtils.isBlank(group) || StringUtils.isBlank(dataId)) {
                //格式错误，直接返回
                ConfigImportItemVo configItem = new ConfigImportItemVo();
                configItem.setItemName(itemName);
                importDataVo.addUnrecognized(configItem);
                continue;
            }

            ConfigAllInfo configInfo = super.createConfigInfo(oneConfigItem);

            ConfigMetadata.ConfigExportItem metaItem = super.getMetaItem(group, dataId);

            if (metaItem != null) {
                configInfo.setAppName(metaItem.getAppName());
            }
            importDataVo.addConfigData(configInfo);
        }
        return importDataVo;
    }
}
