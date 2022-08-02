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

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.config.server.constant.Constants;
import com.alibaba.nacos.config.server.exception.NacosWebException;
import com.alibaba.nacos.config.server.result.code.ResultCodeEnum;
import com.alibaba.nacos.config.server.service.config.AbstractConfigDataReader;
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
public abstract class AbstractZipConfigDataReader extends AbstractConfigDataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractZipConfigDataReader.class);

    protected ZipUtils.UnZipResult unziped;

    public AbstractZipConfigDataReader(String namespace, ZipUtils.UnZipResult unziped) {

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
    public static AbstractZipConfigDataReader getConfigReader(String namespace, MultipartFile file) throws IOException {

        AbstractZipConfigDataReader zipConfigDataReader = null;

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
