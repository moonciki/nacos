package com.alibaba.nacos.config.server.service.config;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.Pair;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.config.server.exception.NacosWebException;
import com.alibaba.nacos.config.server.model.ConfigAllInfo;
import com.alibaba.nacos.config.server.model.ConfigMetadata;
import com.alibaba.nacos.config.server.result.code.ResultCodeEnum;
import com.alibaba.nacos.config.server.utils.GroupKey;
import com.alibaba.nacos.config.server.vo.ConfigDataBatchVo;
import com.alibaba.nacos.config.server.vo.ConfigImportDataVo;
import com.alibaba.nacos.config.server.vo.ConfigImportItemVo;
import com.alibaba.nacos.plugin.encryption.handler.EncryptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * read config file service v2.
 * decoupling import config files.
 * @author ysq
 * @date 2022/7/21 18:15
 */
public abstract class ConfigDataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigDataReader.class);

    protected String namespace;

    protected ConfigDataBatchVo configDataBatchVo;

    protected Map<String, ConfigMetadata.ConfigExportItem> metaMap;

    public ConfigDataReader(String namespace) {
        this.namespace = namespace;
    }

    public ConfigDataReader() {
    }

    /**
     * db config list to map.
     * @param dbConfigList dbConfigList
     * @return Map
     */
    public Map<String, ConfigAllInfo> getDbConfigMap(List<ConfigAllInfo> dbConfigList) {

        Map<String, ConfigAllInfo> dbConfigMap = new HashMap<>();

        if (CollectionUtils.isNotEmpty(dbConfigList)) {
            for (ConfigAllInfo oneConfig : dbConfigList) {

                String dataId = oneConfig.getDataId();
                String group = oneConfig.getGroup();

                String metaKey = GroupKey.getKey(dataId, group);

                dbConfigMap.put(metaKey, oneConfig);
            }
        }
        return dbConfigMap;
    }

    /**
     * get meta item by group and dataId.
     * @param group group
     * @param dataId dataId
     * @return ConfigExportItem
     */
    public ConfigMetadata.ConfigExportItem getMetaItem(String group, String dataId) {

        String metaKey = GroupKey.getKey(dataId, group);
        ConfigMetadata.ConfigExportItem metaItem = metaMap.get(metaKey);

        return metaItem;
    }

    /**
     * create config info by config item.
     * @param configItem configItem
     * @return ConfigAllInfo
     */
    public ConfigAllInfo createConfigInfo(ConfigImportItemVo configItem) {

        String group = configItem.getGroup();
        String dataId = configItem.getDataId();
        String content = configItem.getContent();

        //encrypted
        Pair<String, String> pair = EncryptionHandler.encryptHandler(dataId, content);
        content = pair.getSecond();

        ConfigAllInfo configInfo = new ConfigAllInfo();
        configInfo.setGroup(group);
        configInfo.setDataId(dataId);
        configInfo.setContent(content);
        configInfo.setTenant(this.namespace);
        configInfo.setEncryptedDataKey(pair.getFirst());

        return configInfo;
    }

    /**
     * change meta to map.
     */
    public void createMetaMap() {

        List<ConfigMetadata.ConfigExportItem> metaDataList = configDataBatchVo.getMetaDataList();

        this.metaMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(metaDataList)) {
            for (ConfigMetadata.ConfigExportItem oneMeta: metaDataList) {

                String dataId = oneMeta.getDataId();
                String group = oneMeta.getGroup();

                if (StringUtils.isBlank(dataId) || StringUtils.isBlank(group)) {
                    LOGGER.error("metadata error : dataId : {}, group : {}", dataId, group);
                    throw NacosWebException.createException(ResultCodeEnum.METADATA_ILLEGAL.info("metadata error!"));
                }

                String metaKey = GroupKey.getKey(dataId, group);
                metaMap.put(metaKey, oneMeta);
            }
        }
    }

    /**
     * 读取配置文件.
     * @param dbConfigList dbConfigList 如果有需要时，跟数据库当前数据进行对比.
     * @return ConfigImportDataVo
     */
    public ConfigImportDataVo readConfigDataBatch(List<ConfigAllInfo> dbConfigList) {
        LOGGER.info("readConfigDataBatch-zip : ");

        configDataBatchVo = new ConfigDataBatchVo();

        this.readMetaData();

        this.readConfigInfo();

        this.createMetaMap();

        ConfigImportDataVo configImportDataVo = this.parseImportData(dbConfigList);
        return configImportDataVo;
    }

    /**
     * read meta data.
     */
    public abstract void readMetaData();

    /**
     * read config info.
     */
    public abstract void readConfigInfo();

    /**
     * parse import data .
     * @param dbConfigList dbConfigList
     * @return ConfigImportDataVo
     */
    public abstract ConfigImportDataVo parseImportData(List<ConfigAllInfo> dbConfigList);

}
