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

package com.alibaba.nacos.config.server.service.repository;

import com.alibaba.nacos.config.server.exception.NacosWebException;
import com.alibaba.nacos.config.server.model.ConfigAllInfo;
import com.alibaba.nacos.config.server.model.SameConfigPolicy;
import com.alibaba.nacos.config.server.service.config.AbstractConfigDataReader;
import com.alibaba.nacos.config.server.service.config.impl.AbstractZipConfigDataReader;
import com.alibaba.nacos.config.server.service.repository.impl.ConfigInfoHandlerServiceImpl;
import com.alibaba.nacos.config.server.utils.TimeUtils;
import com.alibaba.nacos.config.server.utils.ZipUtils;
import com.alibaba.nacos.config.server.vo.ConfigBatchResultVo;
import com.alibaba.nacos.config.server.vo.ConfigImportDataVo;
import com.alibaba.nacos.config.server.vo.ConfigImportResultVo;
import com.alibaba.nacos.sys.env.EnvUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.ServletContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * test config handler .
 *
 * @author ysq
 * @date 2022/8/11 11:24
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class ConfigInfoHandlerServiceTest {

    private MockMvc mockmvc;

    @InjectMocks
    private ConfigInfoHandlerServiceImpl configInfoHandlerService;

    @Mock
    private ServletContext servletContext;

    @Mock
    private PersistService persistService;

    @Before
    public void setUp() {
        EnvUtil.setEnvironment(new StandardEnvironment());
        when(servletContext.getContextPath()).thenReturn("/nacos");
        //ReflectionTestUtils.setField(configInfoHandlerService, "configSubService", configSubService);
        ReflectionTestUtils.setField(configInfoHandlerService, "persistService", persistService);
        mockmvc = MockMvcBuilders.standaloneSetup(configInfoHandlerService).build();
    }

    @Test
    public void testImportAndPublishConfigNew() throws Exception {

        final Timestamp time =  TimeUtils.getCurrentTime();
        final String tenant = "";

        final String newDataId = "newDataId.yml";
        final String newGroup = "newGroup";
        final ConfigAllInfo newConfig = new ConfigAllInfo();
        newConfig.setTenant(tenant);
        newConfig.setDataId(newDataId);
        newConfig.setGroup(newGroup);
        //不存在情况
        when(persistService.findConfigAllInfo(newDataId, newGroup, tenant)).thenReturn(null);

        final String dbDataId = "dbDataId.yml";
        final String dbGroup = "dbGroup";
        final ConfigAllInfo dbConfig = new ConfigAllInfo();
        dbConfig.setTenant(tenant);
        dbConfig.setDataId(dbDataId);
        dbConfig.setGroup(dbGroup);

        //存在情况
        when(persistService.findConfigAllInfo(dbDataId, dbGroup, tenant)).thenReturn(dbConfig);

        //when(persistService.addConfigInfo(srcIp, srcUser, configInfo2Save, time, configAdvanceMap, false));

        //create zipUtils
        MockedStatic<ZipUtils> zipUtilsMockedStatic = Mockito.mockStatic(ZipUtils.class);
        List<ZipUtils.ZipItem> zipItems = new ArrayList<>();

        ZipUtils.ZipItem newZipItem = new ZipUtils.ZipItem(newGroup + "/" + newDataId, "#newDataTest");
        ZipUtils.ZipItem dbZipItem = new ZipUtils.ZipItem(dbGroup + "/" + dbDataId, "#dbDataIdTest");

        zipItems.add(newZipItem);
        zipItems.add(dbZipItem);

        ZipUtils.UnZipResult unziped = new ZipUtils.UnZipResult(zipItems, null);
        MockMultipartFile file = new MockMultipartFile("file", "test.zip", "application/zip", "test".getBytes());

        zipUtilsMockedStatic.when(() -> ZipUtils.unzip(file.getBytes())).thenReturn(unziped);

        AbstractConfigDataReader configReader = AbstractZipConfigDataReader.getConfigReader(tenant, file);

        try {
            ConfigImportDataVo configImportDataVo = configReader.readConfigDataBatch(null);

            //开始处理文件数据
            final String srcIp = "127.0.0.1";
            String requestIpApp = "test_requestIpApp";
            String srcUser = "nacos";

            ConfigImportResultVo batchImportResult = configInfoHandlerService.batchInsertOrUpdate(configImportDataVo, srcUser, srcIp,
                    null, time, false, SameConfigPolicy.ABORT);

            ConfigBatchResultVo importResultVo = configInfoHandlerService.notifyConfigAndReturn(batchImportResult, time, requestIpApp);

            Assert.assertNotNull(importResultVo);

        } catch (NacosWebException e) {
            //当文件解析错误时，会通过该异常返回信息

            Integer errorCode = e.getErrorCode();
            String errorMsg = e.getErrorMsg();

            Assert.assertNotNull(errorCode);
            Assert.assertNotNull(errorMsg);
        }

        zipUtilsMockedStatic.close();

    }

}