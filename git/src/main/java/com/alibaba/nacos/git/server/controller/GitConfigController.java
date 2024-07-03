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

import com.alibaba.nacos.auth.annotation.Secured;
import com.alibaba.nacos.common.model.RestResult;
import com.alibaba.nacos.common.model.RestResultUtils;
import com.alibaba.nacos.config.server.exception.NacosWebException;
import com.alibaba.nacos.config.server.utils.RequestUtil;
import com.alibaba.nacos.config.server.vo.ConfigImportResultVo;
import com.alibaba.nacos.git.server.enums.GitResponseEnum;
import com.alibaba.nacos.git.server.service.git.GitConfigService;
import com.alibaba.nacos.git.server.vo.GitCommitStatus;
import com.alibaba.nacos.git.server.vo.TenantGitVo;
import com.alibaba.nacos.plugin.auth.constant.ActionTypes;
import com.alibaba.nacos.plugin.auth.constant.SignType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * git library config file.
 *
 * @author ysq
 * @date 2021/10/19 11:52
 */
@RestController
@RequestMapping("/v1/git")
public class GitConfigController {

    @Autowired
    private GitConfigService gitConfigService;

    /**
     * Get namespace git config.
     * without private key.
     * @return TenantGitVo list
     */
    @GetMapping("/getNamespaceGit")
    @Secured(action = ActionTypes.READ, signType = SignType.CONFIG)
    public RestResult<TenantGitVo> getNamespaceGit(String tenantId) {

        if (tenantId == null) {
            throw NacosWebException.createException(GitResponseEnum.request_error.info("[tenantId] must not be null!"));
        }
        TenantGitVo namespaceGit = gitConfigService.getBaseNamespaceGit(tenantId);
        return RestResultUtils.success(namespaceGit);
    }

    /**
     * save namespace git config.
     * @return TenantGitVo list
     */
    @PostMapping("/saveNamespaceGit")
    @Secured(action = ActionTypes.WRITE, signType = SignType.CONFIG)
    public RestResult<TenantGitVo> saveNamespaceGit(@RequestBody TenantGitVo tenantGitVo) {

        if (tenantGitVo == null) {
            throw NacosWebException.createException(GitResponseEnum.request_error.info("params error!"));
        }

        TenantGitVo namespaceGit = gitConfigService.saveNamespaceGit(tenantGitVo);
        return RestResultUtils.success(namespaceGit);
    }

    /**
     * delete namespace git config.
     * @return RestResult
     */
    @PostMapping("/removeNamespaceGit")
    @Secured(action = ActionTypes.WRITE, signType = SignType.CONFIG)
    public RestResult removeNamespaceGit(String tenantId) {

        if (tenantId == null) {
            throw NacosWebException.createException(GitResponseEnum.request_error.info("[tenantId] must not be null!"));
        }

        gitConfigService.removeNamespaceGit(tenantId);
        return RestResultUtils.success();
    }

    /**
     * init git repo and return commit status.
     * @return RestResult
     */
    @PostMapping("/getCommitStatus")
    @Secured(action = ActionTypes.READ, signType = SignType.CONFIG)
    public RestResult<GitCommitStatus> getCommitStatus(String tenantId) {

        if (tenantId == null) {
            throw NacosWebException.createException(GitResponseEnum.request_error.info("[tenantId] must not be null!"));
        }

        GitCommitStatus commitStatus = gitConfigService.getCommitStatus(tenantId);
        return RestResultUtils.success(commitStatus);
    }

    /**
     * sync config.
     * @param tenantId tenantId
     * @return RestResult
     */
    @PostMapping("/syncConfig")
    @Secured(action = ActionTypes.WRITE, signType = SignType.CONFIG)
    public RestResult<ConfigImportResultVo> syncConfig(HttpServletRequest request, String srcUser, String tenantId) {

        if (tenantId == null) {
            throw NacosWebException.createException(GitResponseEnum.request_error.info("[tenantId] must not be null!"));
        }

        final String srcIp = RequestUtil.getRemoteIp(request);
        String requestIpApp = RequestUtil.getAppName(request);

        ConfigImportResultVo importResultVo = gitConfigService.syncConfig(tenantId, srcUser, srcIp, requestIpApp);
        return RestResultUtils.success(importResultVo);
    }

}
