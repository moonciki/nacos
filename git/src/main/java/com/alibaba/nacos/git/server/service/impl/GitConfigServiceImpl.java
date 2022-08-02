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

package com.alibaba.nacos.git.server.service.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.config.server.exception.NacosWebException;
import com.alibaba.nacos.config.server.model.ConfigAllInfo;
import com.alibaba.nacos.config.server.model.SameConfigPolicy;
import com.alibaba.nacos.config.server.service.config.AbstractConfigDataReader;
import com.alibaba.nacos.config.server.service.repository.ConfigInfoHandlerService;
import com.alibaba.nacos.config.server.service.repository.PersistService;
import com.alibaba.nacos.config.server.utils.TimeUtils;
import com.alibaba.nacos.config.server.vo.ConfigImportDataVo;
import com.alibaba.nacos.config.server.vo.ConfigImportResultVo;
import com.alibaba.nacos.git.server.dao.TenantGitDao;
import com.alibaba.nacos.git.server.dao.impl.BaseExternalDaompl;
import com.alibaba.nacos.git.server.enums.ResponseEnum;
import com.alibaba.nacos.git.server.model.TenantGit;
import com.alibaba.nacos.git.server.service.GitConfigService;
import com.alibaba.nacos.git.server.service.JgitOperationService;
import com.alibaba.nacos.git.server.service.config.impl.FolderConfigDataReader;
import com.alibaba.nacos.git.server.vo.GitCommitStatus;
import com.alibaba.nacos.git.server.vo.GitCommitVo;
import com.alibaba.nacos.git.server.vo.TenantGitVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

/**
 * git config.
 *
 * @author ysq
 */
@Service
public class GitConfigServiceImpl<E> extends BaseExternalDaompl implements GitConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitConfigServiceImpl.class);

    @Autowired
    private TenantGitDao tenantGitDao;

    @Autowired
    private JgitOperationService jgitOperationService;

    @Autowired(required = false)
    private PersistService persistService;

    @Autowired
    private ConfigInfoHandlerService configInfoHandlerService;

    @Override
    public TenantGitVo getBaseNamespaceGit(String tenantId) {

        TenantGit dbTenantGit = tenantGitDao.getFullTenantGit(tenantId);

        if (dbTenantGit == null) {
            return null;
        }

        String passphrase = dbTenantGit.getPassphrase();
        String privateKey = dbTenantGit.getPrivateKey();
        String password = dbTenantGit.getPassword();

        TenantGitVo tenantGitBase = new TenantGitVo();
        BeanUtils.copyProperties(dbTenantGit, tenantGitBase, TenantGitVo.class);

        if (StringUtils.isNotBlank(privateKey)) {
            //auth by privateKey
            tenantGitBase.setAuthType(2);
        } else {
            tenantGitBase.setAuthType(1);
        }

        tenantGitBase.setPassphrase(null);
        tenantGitBase.setPrivateKey(null);
        tenantGitBase.setPassword(null);

        return tenantGitBase;
    }

    /**
     * check param .
     * @param tenantGitVo tenantGitVo
     */
    private void checkSaveParam(TenantGitVo tenantGitVo) {

        String tenantId = tenantGitVo.getTenantId();

        if (tenantId == null) {
            throw NacosWebException.createException(ResponseEnum.request_error.info("[tenantId] must not be null!"));
        }
        String uri = tenantGitVo.getUri();
        if (StringUtils.isBlank(uri)) {
            throw NacosWebException.createException(ResponseEnum.request_error.info("[uri] must not be null!"));
        }

    }

    @Override
    public TenantGitVo saveNamespaceGit(TenantGitVo tenantGitVo) {

        this.checkSaveParam(tenantGitVo);

        TenantGit tenantGitReq = new TenantGit();
        BeanUtils.copyProperties(tenantGitVo, tenantGitReq);

        String tenantId = tenantGitReq.getTenantId();

        TenantGit dbTenantGit = tenantGitDao.getFullTenantGit(tenantId);

        Long id = tenantGitReq.getId();

        String userName = tenantGitReq.getUserName();
        String password = tenantGitReq.getPassword();
        String privateKey = tenantGitReq.getPrivateKey();

        if (dbTenantGit == null) {

            if (StringUtils.isBlank(privateKey)) {
                if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
                    throw NacosWebException.createException(ResponseEnum.request_error
                            .info("[userName], [password] or [privateKey] must not be null!"));
                }
            }

            //insert
            tenantGitDao.insertNamespaceGit(tenantGitReq);

        } else {

            if (StringUtils.isBlank(privateKey) && StringUtils.isBlank(password)) {
                //都为空时，没变化
                tenantGitReq.setPrivateKey(dbTenantGit.getPrivateKey());
                tenantGitReq.setPassword(dbTenantGit.getPrivateKey());
            }

            //update
            tenantGitDao.updateNamespaceGit(tenantGitReq);
            //移除git repo
            jgitOperationService.clearRepository(dbTenantGit);

        }

        TenantGitVo gitResult = this.getBaseNamespaceGit(tenantId);
        return gitResult;
    }

    @Override
    public void removeNamespaceGit(String tenantId) {

        TenantGit dbTenantGit = tenantGitDao.getFullTenantGit(tenantId);

        if (dbTenantGit == null) {
            return;
        }

        jgitOperationService.destroyRepository(dbTenantGit);

        tenantGitDao.deleteNamespaceGit(tenantId);

        LOGGER.info("deleteNamespaceGit : " + tenantId);

    }

    @Override
    public GitCommitStatus getCommitStatus(String tenantId) {

        TenantGit dbTenantGit = tenantGitDao.getFullTenantGit(tenantId);

        if (dbTenantGit == null) {
            return null;
        }

        //查询服务端最新提交日志
        GitCommitVo newestCommit = jgitOperationService.getNewestCommit(dbTenantGit);

        GitCommitStatus syncStatus = new GitCommitStatus();
        BeanUtils.copyProperties(newestCommit, syncStatus, GitCommitStatus.class);

        syncStatus.setRemoteCommitId(newestCommit.getCommitId());

        syncStatus.setUri(dbTenantGit.getUri());
        syncStatus.setSyncTime(dbTenantGit.getSyncTime());
        syncStatus.setDbCommitId(dbTenantGit.getCommitId());

        return syncStatus;
    }

    /**
     * import config data with folder.
     * @param dbTenantGit dbTenantGit
     * @param configFolder configFolder
     * @param srcUser srcUser
     * @param srcIp srcIp
     * @param requestIpApp requestIpApp
     * @return ConfigBatchResultVo
     */
    public ConfigImportResultVo importConfigWithFolder(TenantGit dbTenantGit, File configFolder, String srcUser, String srcIp, String requestIpApp) {

        String namespace = dbTenantGit.getTenantId();

        List<ConfigAllInfo> dbConfigList = persistService.findAllConfigInfo4Export(null, null, namespace, null, null);
        AbstractConfigDataReader configReader = FolderConfigDataReader.getConfigReader(namespace, configFolder);

        ConfigImportDataVo configImportDataVo = configReader.readConfigDataBatch(dbConfigList);

        final Timestamp time = TimeUtils.getCurrentTime();

        ConfigImportResultVo batchImportResult = configInfoHandlerService.batchInsertOrUpdate(configImportDataVo, srcUser, srcIp,
                null, time, false, SameConfigPolicy.OVERWRITE);

        //notify
        List<ConfigAllInfo> savedConfigList = configInfoHandlerService.notifyConfigChange(batchImportResult, time, requestIpApp);

        return batchImportResult;
    }

    @Override
    public ConfigImportResultVo syncConfig(String tenantId, String srcUser, String srcIp, String requestIpApp) {

        TenantGit dbTenantGit = tenantGitDao.getFullTenantGit(tenantId);

        if (dbTenantGit == null) {
            return null;
        }

        //查询服务端最新提交日志
        GitCommitVo newestCommit = jgitOperationService.getNewestCommit(dbTenantGit);

        if (newestCommit == null) {
            return null;
        }

        String dbCommitId = dbTenantGit.getCommitId();
        String remoteCommitId = newestCommit.getCommitId();

        if (remoteCommitId == null) {
            return null;
        }

        ConfigImportResultVo importResultVo = null;

        if (StringUtils.isBlank(dbCommitId) || !dbCommitId.equals(remoteCommitId)) {
            LOGGER.info("start syncConfig : [" + tenantId + "]");

            File configFolder = jgitOperationService.getConfigFolder(dbTenantGit);

            if (!configFolder.exists()) {
                throw NacosWebException.createException(ResponseEnum.request_error.info("git repository don't exist!"));
            }

            importResultVo = this.importConfigWithFolder(dbTenantGit, configFolder, srcUser, srcIp, requestIpApp);

        }

        return importResultVo;
    }

}
