package com.alibaba.nacos.config.server.service.repository.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.config.server.enums.FileTypeEnum;
import com.alibaba.nacos.config.server.model.ConfigAllInfo;
import com.alibaba.nacos.config.server.model.ConfigInfo;
import com.alibaba.nacos.config.server.model.SameConfigPolicy;
import com.alibaba.nacos.config.server.model.event.ConfigDataChangeEvent;
import com.alibaba.nacos.config.server.service.ConfigChangePublisher;
import com.alibaba.nacos.config.server.service.repository.ConfigInfoHandlerService;
import com.alibaba.nacos.config.server.service.repository.PersistService;
import com.alibaba.nacos.config.server.service.trace.ConfigTraceService;
import com.alibaba.nacos.config.server.utils.ParamUtils;
import com.alibaba.nacos.config.server.vo.ConfigBatchResultVo;
import com.alibaba.nacos.config.server.vo.ConfigImportDataVo;
import com.alibaba.nacos.config.server.vo.ConfigImportItemVo;
import com.alibaba.nacos.config.server.vo.ConfigImportResultVo;
import com.alibaba.nacos.sys.utils.InetUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.nacos.config.server.utils.LogUtil.DEFAULT_LOG;

/**
 * config info service .
 *
 * @author ysq
 * @date 2022/8/5 15:26
 */
@Service
public class ConfigInfoHandlerServiceImpl implements ConfigInfoHandlerService {

    /**
     * constant variables.
     */
    public static final String SPOT = ".";

    private PersistService persistService;

    public ConfigInfoHandlerServiceImpl(PersistService persistService) {
        this.persistService = persistService;
    }

    /**
     * turn to configInfo obj.
     * @param configInfo configInfo
     * @return ConfigInfo
     */
    private ConfigInfo createConfigInfo(ConfigAllInfo configInfo) {
        ConfigInfo configInfoResult = new ConfigInfo(configInfo.getDataId(), configInfo.getGroup(),
                configInfo.getTenant(), configInfo.getAppName(), configInfo.getContent());
        configInfoResult.setEncryptedDataKey(
                configInfo.getEncryptedDataKey() == null ? "" : configInfo.getEncryptedDataKey());

        return configInfoResult;
    }

    /**
     * clear content for web site.
     * @param configInfo configInfo
     * @return ConfigAllInfo
     */
    private ConfigAllInfo createBaseConfig(ConfigInfo configInfo) {

        String tenant = configInfo.getTenant();
        String group = configInfo.getGroup();
        String dataId = configInfo.getDataId();

        ConfigAllInfo configVo = new ConfigAllInfo();
        configVo.setGroup(group);
        configVo.setDataId(dataId);
        configVo.setTenant(tenant);

        return configVo;
    }

    /**
     * return error item to web.
     * @param batchResult batchResult
     * @param configInfo configInfo
     * @param errorMsg errorMsg nullable
     */
    private void addErrorItem(ConfigImportResultVo batchResult, ConfigInfo configInfo, String errorMsg) {
        String group = configInfo.getGroup();
        String dataId = configInfo.getDataId();
        String itemName = group + "/" + dataId;

        if (StringUtils.isNotBlank(errorMsg)) {
            itemName = errorMsg + ":" + itemName;
        }

        ConfigImportItemVo errorItem = new ConfigImportItemVo();
        errorItem.setItemName(itemName);
        batchResult.addError(errorItem);
    }

    private void addNewItem(ConfigImportResultVo batchResult, ConfigInfo configInfo) {
        ConfigAllInfo configVo = this.createBaseConfig(configInfo);
        batchResult.addNew(configVo);
    }

    private void addSkipItem(ConfigImportResultVo batchResult, ConfigInfo configInfo) {
        ConfigAllInfo configVo = this.createBaseConfig(configInfo);
        batchResult.addSkip(configVo);
    }

    private void addModifyItem(ConfigImportResultVo batchResult, ConfigInfo configInfo) {
        ConfigAllInfo configVo = this.createBaseConfig(configInfo);
        batchResult.addModify(configVo);
    }

    private void addDeleteItem(ConfigImportResultVo batchResult, ConfigInfo configInfo) {
        ConfigAllInfo configVo = this.createBaseConfig(configInfo);
        batchResult.addDelete(configVo);
    }

    private void addNoChangeItem(ConfigImportResultVo batchResult, ConfigInfo configInfo) {
        ConfigAllInfo configVo = this.createBaseConfig(configInfo);
        batchResult.addNoChange(configVo);
    }

    /**
     * create configAdvanceMap.
     * @param configAllInfo configAllInfo
     * @param configAdvanceMap configAdvanceMap
     * @return configAdvanceMap
     */
    private Map<String, Object> getConfigAdvanceMap(ConfigAllInfo configAllInfo, Map<String, Object> configAdvanceMap) {

        String dataId = configAllInfo.getDataId();
        String type = configAllInfo.getType();
        final String desc = configAllInfo.getDesc();

        if (StringUtils.isBlank(type)) {
            // simple judgment of file type based on suffix
            if (dataId.contains(SPOT)) {
                int spotIndex = dataId.lastIndexOf(SPOT);
                String extName = dataId.substring(spotIndex + 1);
                FileTypeEnum fileTypeEnum = FileTypeEnum.getFileTypeEnumByFileExtensionOrFileType(extName);
                type = fileTypeEnum.getFileType();
            } else {
                type = FileTypeEnum.getFileTypeEnumByFileExtensionOrFileType(null).getFileType();
            }
            //set type
            configAllInfo.setType(type);
        }
        if (configAdvanceMap == null) {
            configAdvanceMap = new HashMap<>(16);
        }
        configAdvanceMap.put("type", type);
        configAdvanceMap.put("desc", desc);

        return configAdvanceMap;
    }

    /**
     * check config same.
     * @param dbConfigInfo dbConfigInfo
     * @param reqConfigInfo reqConfigInfo
     * @return boolean
     */
    public boolean checkConfigSame(ConfigAllInfo dbConfigInfo, ConfigAllInfo reqConfigInfo) {
        //TODO: check config same with field:
        //group, dataId, content(or md5), desc, type, appName
        //if req field is null, then same.

        return false;
    }

    /**
     * 保存批量数据 .
     * @param batchResult batchResult
     * @param configDataList configDataList
     * @param srcUser srcUser
     * @param srcIp srcIp
     * @param configAdvanceMap configAdvanceMap
     * @param time time
     * @param notify notify
     * @param policy policy
     */
    private void saveImportData(ConfigImportResultVo batchResult, List<ConfigAllInfo> configDataList, String srcUser, String srcIp,
                             Map<String, Object> configAdvanceMap, Timestamp time, boolean notify, SameConfigPolicy policy) {

        if (CollectionUtils.isEmpty(configDataList)) {
            return;
        }

        int i = -1;
        for (ConfigAllInfo oneConfigTmp: configDataList) {
            i++;

            String dataId = oneConfigTmp.getDataId();
            String group = oneConfigTmp.getGroup();
            String tenant = oneConfigTmp.getTenant();

            try {
                ParamUtils.checkParam(oneConfigTmp.getDataId(), oneConfigTmp.getGroup(),
                        "datumId", oneConfigTmp.getContent());
            } catch (Exception e) {
                DEFAULT_LOG.error("config verification failed", e);

                this.addErrorItem(batchResult, oneConfigTmp, "格式错误");
                continue;
            }

            ConfigInfo configInfo2Save = this.createConfigInfo(oneConfigTmp);

            configAdvanceMap = this.getConfigAdvanceMap(oneConfigTmp, configAdvanceMap);

            ConfigAllInfo dbConfigInfo = null;
            boolean configExist = false;
            try {

                dbConfigInfo = persistService.findConfigAllInfo(dataId, group, tenant);

                if (dbConfigInfo != null) {
                    configExist = true;
                } else {
                    persistService.addConfigInfo(srcIp, srcUser, configInfo2Save, time, configAdvanceMap, false);
                    this.addNewItem(batchResult, configInfo2Save);
                }
            } catch (Exception e) {
                if (!StringUtils.contains(e.toString(), "DuplicateKeyException")) {
                    DEFAULT_LOG.error("addConfigInfo error : ", e);
                    this.addErrorItem(batchResult, configInfo2Save, "保存失败");
                    continue;
                }
                configExist = true;
            }

            if (configExist) {
                boolean configSame = this.checkConfigSame(dbConfigInfo, oneConfigTmp);

                if (configSame) {
                    this.addNoChangeItem(batchResult, configInfo2Save);
                } else {
                    //存在则更新
                    boolean keepGoing = this.saveModify(batchResult, configInfo2Save, srcUser, srcIp, configAdvanceMap, time, notify, policy);
                    if (!keepGoing) {
                        //终止
                        for (int j = (i + 1); j < configDataList.size(); j++) {
                            ConfigAllInfo skipConfigInfo = configDataList.get(j);
                            this.addSkipItem(batchResult, skipConfigInfo);
                        }
                        break;
                    }
                }
            }
        }
    }

    private boolean saveModify(ConfigImportResultVo batchResult, ConfigInfo modifyConfig, String srcUser, String srcIp,
                                Map<String, Object> configAdvanceMap, Timestamp time, boolean notify, SameConfigPolicy policy) {

        if (modifyConfig == null) {
            return true;
        }

        // uniqueness constraint conflict
        if (policy == null || SameConfigPolicy.ABORT.equals(policy)) {
            //终止
            this.addErrorItem(batchResult, modifyConfig, null);

            return false;

        } else if (SameConfigPolicy.SKIP.equals(policy)) {

            this.addSkipItem(batchResult, modifyConfig);

        } else if (SameConfigPolicy.OVERWRITE.equals(policy)) {

            persistService.updateConfigInfo(modifyConfig, srcIp, srcUser, time, configAdvanceMap, notify);
            this.addModifyItem(batchResult, modifyConfig);

        }
        return true;
    }

    private void deleteImportData(ConfigImportResultVo batchResult, List<ConfigAllInfo> deleteList, String srcUser, String srcIp) {

        if (CollectionUtils.isEmpty(deleteList)) {
            return;
        }

        for (ConfigAllInfo oneDelTmp: deleteList) {
            String dataId = oneDelTmp.getDataId();
            String group = oneDelTmp.getGroup();
            String tenant = oneDelTmp.getTenant();

            persistService.removeConfigInfo(dataId, group, tenant, srcIp, srcUser);
            this.addDeleteItem(batchResult, oneDelTmp);
        }

    }

    @Override
    public List<ConfigImportItemVo> toConfigItemList(List<ConfigAllInfo> configInfoList) {
        if (configInfoList == null) {
            return null;
        }

        List<ConfigImportItemVo> configItemList = null;

        for (ConfigAllInfo oneConfig: configInfoList) {

            ConfigImportItemVo configItem = new ConfigImportItemVo();

            configItem.setDataId(oneConfig.getDataId());
            configItem.setGroup(oneConfig.getGroup());

            if (configItemList == null) {
                configItemList = new ArrayList<>();
            }
            configItemList.add(configItem);
        }
        return configItemList;
    }

    @Override
    public ConfigImportResultVo batchInsertOrUpdate(final ConfigImportDataVo configImportData, String srcUser, String srcIp,
                                                    Map<String, Object> configAdvanceMap, Timestamp time,
                                                    boolean notify, SameConfigPolicy policy) {
        ConfigImportResultVo batchResult = new ConfigImportResultVo();

        List<ConfigAllInfo> configDataList = configImportData.getConfigDataList();
        List<ConfigAllInfo> deleteList = configImportData.getDeleteList();
        List<ConfigImportItemVo> unrecognizedList = configImportData.getUnrecognizedList();

        //未变更的在本方法判断
        //List<ConfigAllInfo> noChangeList = configImportData.getNoChangeList();

        if (CollectionUtils.isNotEmpty(unrecognizedList)) {
            for (ConfigImportItemVo oneConfigTmp: unrecognizedList) {

                ConfigImportItemVo unrecognConfig = new ConfigImportItemVo();
                unrecognConfig.setItemName(oneConfigTmp.getItemName());
                unrecognConfig.setDataId(oneConfigTmp.getDataId());
                unrecognConfig.setGroup(oneConfigTmp.getGroup());

                batchResult.addError(oneConfigTmp);
            }
        }

        this.saveImportData(batchResult, configDataList, srcUser, srcIp, configAdvanceMap, time, notify, policy);

        this.deleteImportData(batchResult, deleteList, srcUser, srcIp);

        return batchResult;
    }

    @Override
    public List<ConfigAllInfo> notifyConfigChange(ConfigImportResultVo configImportResultVo, Timestamp time, String requestIpApp) {
        //notify
        List<ConfigAllInfo> savedConfigList = new ArrayList<>();

        List<ConfigAllInfo> newList = configImportResultVo.getNewList();
        List<ConfigAllInfo> modifyList = configImportResultVo.getModifyList();

        if (CollectionUtils.isNotEmpty(newList)) {
            savedConfigList.addAll(newList);
        }
        if (CollectionUtils.isNotEmpty(modifyList)) {
            savedConfigList.addAll(modifyList);
        }

        for (ConfigInfo configInfo : savedConfigList) {
            ConfigChangePublisher.notifyConfigChange(
                    new ConfigDataChangeEvent(false, configInfo.getDataId(), configInfo.getGroup(),
                            configInfo.getTenant(), time.getTime()));
            ConfigTraceService.logPersistenceEvent(configInfo.getDataId(), configInfo.getGroup(),
                    configInfo.getTenant(), requestIpApp, time.getTime(), InetUtils.getSelfIP(),
                    ConfigTraceService.PERSISTENCE_EVENT_PUB, configInfo.getContent());
        }

        return savedConfigList;
    }

    @Override
    public ConfigBatchResultVo notifyConfigAndReturn(ConfigImportResultVo configImportResultVo, Timestamp time, String requestIpApp) {
        //notify
        List<ConfigAllInfo> savedConfigList = this.notifyConfigChange(configImportResultVo, time, requestIpApp);

        List<ConfigAllInfo> noChangeList = configImportResultVo.getNoChangeList();
        if (CollectionUtils.isNotEmpty(noChangeList)) {
            //未修改的不需要通知，但是需要作为成功的返回
            savedConfigList.addAll(noChangeList);
        }

        List<ConfigImportItemVo> succDataList = this.toConfigItemList(savedConfigList);

        //return data
        ConfigBatchResultVo batchResultVo = new ConfigBatchResultVo();

        batchResultVo.setSuccCount(succDataList.size());
        batchResultVo.setSuccData(succDataList);

        List<ConfigAllInfo> skipList = configImportResultVo.getSkipList();

        List<ConfigImportItemVo> failData = configImportResultVo.getErrorList();
        List<ConfigImportItemVo> unrecognizedData = configImportResultVo.getUnrecognizedList();

        List<ConfigImportItemVo> skipData = this.toConfigItemList(skipList);;

        if (unrecognizedData != null) {
            batchResultVo.setUnrecognizedCount(unrecognizedData.size());
            batchResultVo.setUnrecognizedData(unrecognizedData);
        }
        if (skipData != null) {
            batchResultVo.setSkipCount(skipData.size());
            batchResultVo.setSkipData(skipData);
        }
        batchResultVo.setFailData(failData);

        return batchResultVo;
    }

}
