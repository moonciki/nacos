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

package com.alibaba.nacos.git.server.controller;

import com.alibaba.nacos.common.model.RestResult;
import com.alibaba.nacos.git.server.service.git.GitConfigService;
import com.alibaba.nacos.git.server.vo.TenantGitVo;
import com.alibaba.nacos.sys.env.EnvUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.ServletContext;

import static org.mockito.Mockito.when;

/**
 * git config controller test .
 *
 * @author ysq
 * @date 2022/11/2 15:05
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MockServletContext.class})
@WebAppConfiguration
public class GitConfigControllerTest {

    private MockMvc mockmvc;

    @InjectMocks
    private GitConfigController gitConfigController;

    @Mock
    private GitConfigService gitConfigService;

    @Mock
    private ServletContext servletContext;

    @Before
    public void setUp() {
        EnvUtil.setEnvironment(new StandardEnvironment());
        when(servletContext.getContextPath()).thenReturn("/nacos");
        //ReflectionTestUtils.setField(gitConfigController, "gitConfigService", gitConfigService);

        //ReflectionTestUtils.setField(gitConfigService, "tenantGitDao", tenantGitDao);

        mockmvc = MockMvcBuilders.standaloneSetup(gitConfigController).build();
    }

    @Test
    public void testGetNamespaceGit() {

        String tenantId = "";

        when(gitConfigService.getBaseNamespaceGit(tenantId)).thenReturn(null);

        RestResult<TenantGitVo> namespaceGit = gitConfigController.getNamespaceGit(tenantId);

        Assert.assertNull(namespaceGit.getData());

        String newTenantId = "new_test";

        TenantGitVo newGit = new TenantGitVo();
        newGit.setId(1L);
        newGit.setUserName("test");
        newGit.setTenantId(newTenantId);

        when(gitConfigService.getBaseNamespaceGit(newTenantId)).thenReturn(newGit);

        RestResult<TenantGitVo> namespaceGitNew = gitConfigController.getNamespaceGit(newTenantId);

        Assert.assertEquals(newTenantId, namespaceGitNew.getData().getTenantId());

    }

    @Test
    public void testSaveNamespaceGit() {

        String

        TenantGitVo tenantGitVo = new TenantGitVo();
        tenantGitVo.setTenantId("");
        //tenantGitVo.setrepoUuid;
        tenantGitVo.setUri("http://test.git.com/xxx_config.git");
        tenantGitVo.setUserName("test");
        tenantGitVo.setPassword("test");

        //tenantGitVo.setPath();
        tenantGitVo.setBranch("master");

        when(gitConfigService.saveNamespaceGit(tenantGitVo)).thenReturn(tenantGitVo);

        RestResult<TenantGitVo> tenantGitVoRestResult = gitConfigController.saveNamespaceGit(tenantGitVo);

        Assert.assertEquals(tenantGitVoRestResult.getData().getUri(), tenantGitVo.getUri());

    }

}