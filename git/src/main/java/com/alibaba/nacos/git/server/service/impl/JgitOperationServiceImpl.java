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
import com.alibaba.nacos.git.server.git.opt.GitEnvironmentRepository;
import com.alibaba.nacos.git.server.vo.GitCommitVo;
import com.alibaba.nacos.git.server.model.TenantGit;
import com.alibaba.nacos.git.server.service.JgitOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * git opt.
 * @author ysq
 * @date 2022/6/16 13:37
 */
@Service
public class JgitOperationServiceImpl implements JgitOperationService {

    static final Logger LOGGER = LoggerFactory.getLogger(JgitOperationServiceImpl.class);

    boolean initialized = false;

    /**
     * jgit repository cache .
     */
    private static final Map<String, GitEnvironmentRepository> JGIT_REPOSITORY_MAP = new HashMap<>();

    @Override
    public void clearRepository(TenantGit tenantGit) {
        if (tenantGit == null) {
            return;
        }

        GitEnvironmentRepository gitEnvironmentRepository = this.getJGitRepository(tenantGit);

        gitEnvironmentRepository.clear();

        LOGGER.info("clearRepository : " + tenantGit.getTenantId());

    }

    @Override
    public void destroyRepository(TenantGit tenantGit) {

        GitEnvironmentRepository gitEnvironmentRepository = this.getJGitRepository(tenantGit);

        gitEnvironmentRepository.destroy();

        JGIT_REPOSITORY_MAP.remove(tenantGit.getTenantId());

        LOGGER.info("destroyRepository : " + tenantGit.getTenantId());
    }

    /**
     * get or init jgit .
     * @param tenantGit tenantGit
     * @return JGitEnvironmentRepository env
     */
    private GitEnvironmentRepository getJGitRepository(TenantGit tenantGit) {

        String tenantId = tenantGit.getTenantId();

        GitEnvironmentRepository gitEnvironmentRepository = JGIT_REPOSITORY_MAP.get(tenantId);
        if (gitEnvironmentRepository == null) {
            gitEnvironmentRepository = new GitEnvironmentRepository(tenantGit);
            JGIT_REPOSITORY_MAP.put(tenantId, gitEnvironmentRepository);
        }
        return gitEnvironmentRepository;
    }

    @Override
    public GitCommitVo getNewestCommit(TenantGit tenantGit) {

        GitEnvironmentRepository jgitRepo = this.getJGitRepository(tenantGit);

        GitCommitVo remoteCommitVo = jgitRepo.refresh();

        return remoteCommitVo;
    }

    @Override
    public File getConfigFolder(TenantGit tenantGit) {

        File configFolder = null;

        GitEnvironmentRepository jgitRepo = this.getJGitRepository(tenantGit);

        File repoFolder = jgitRepo.getConfigRepoFolder();

        String path = tenantGit.getPath();

        if (StringUtils.isNotBlank(path)) {
            configFolder = new File(repoFolder, path);
        } else {
            configFolder = repoFolder;
        }

        return configFolder;
    }

}
