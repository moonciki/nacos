package com.alibaba.nacos.config.server.service.config.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.config.server.constant.Constants;
import com.alibaba.nacos.config.server.exception.NacosWebException;
import com.alibaba.nacos.config.server.result.code.ResultCodeEnum;
import com.alibaba.nacos.config.server.service.config.ConfigDataReader;
import com.alibaba.nacos.config.server.utils.ZipUtils;
import com.alibaba.nacos.config.server.vo.ConfigImportItemVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * import zip v1/v2 reader.
 *
 * @author ysq
 * @date 2022/7/22 10:07
 */
public abstract class ZipConfigDataReader extends ConfigDataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipConfigDataReader.class);

    protected ZipUtils.UnZipResult unziped;

    public ZipConfigDataReader(String namespace, ZipUtils.UnZipResult unziped) {

        this.namespace = namespace;
        this.unziped = unziped;

    }

    @Override
    public void readConfigInfo() {

        List<ZipUtils.ZipItem> zipItemList = unziped.getZipItemList();

        int itemNameLength = 2;
        zipItemList.forEach(item -> {
            String itemName = item.getItemName();
            String[] groupAdnDataId = itemName.split(Constants.CONFIG_EXPORT_ITEM_FILE_SEPARATOR);

            String dataId = null;
            String group = null;
            if (groupAdnDataId.length != itemNameLength) {
                //dataId = itemName;
            } else {
                group = groupAdnDataId[0];
                dataId = groupAdnDataId[1];
            }

            ConfigImportItemVo configItem = new ConfigImportItemVo();
            configItem.setItemName(itemName);
            configItem.setGroup(group);
            configItem.setDataId(dataId);

            if (StringUtils.isNotBlank(group) && StringUtils.isNotBlank(dataId)) {
                //when group is null, needn't to get content.
                String content = item.getItemData();
                configItem.setContent(content);
            }
            configDataBatchVo.addConfigInfo(configItem);
        });

        if (CollectionUtils.isEmpty(configDataBatchVo.getConfigItemList())) {
            throw NacosWebException.createException(ResultCodeEnum.DATA_EMPTY.info());
        }

    }

    /**
     * get zip config reader.
     * @param namespace namespace
     * @param file file
     * @return ZipConfigDataReader
     * @throws IOException e
     */
    public static ZipConfigDataReader getConfigReader(String namespace, MultipartFile file) throws IOException {

        ZipConfigDataReader zipConfigDataReader = null;

        ZipUtils.UnZipResult unziped = ZipUtils.unzip(file.getBytes());

        ZipUtils.ZipItem metaDataItem = unziped.getMetaDataItem();

        if (metaDataItem != null && Constants.CONFIG_EXPORT_METADATA_NEW.equals(metaDataItem.getItemName())) {
            // v2
            zipConfigDataReader = new ZipConfigDataReaderV2(namespace, unziped);
        } else {
            // v1
            zipConfigDataReader = new ZipConfigDataReaderV1(namespace, unziped);
        }

        return zipConfigDataReader;
    }

}
