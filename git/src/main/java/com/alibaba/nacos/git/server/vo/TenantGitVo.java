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

package com.alibaba.nacos.git.server.vo;

import java.util.Date;

/**
 * 命名空间git 配置.
 *
 * @author ysq
 * @date 2022/6/13 15:20
 */
public class TenantGitVo {

    /**
     * id.
     */
    private Long id;

    /**
     * 命名空间id.
     */
    private String tenantId;

    private String repoUuid;

    /**
     * git uri.
     */
    private String uri;

    /**
     * 用户名.
     */
    private String userName;

    /**
     * 密码（与私钥二选一）.
     */
    private String password;

    /**
     * 私钥口令.
     */
    private String passphrase;

    /**
     * git私钥.
     */
    private String privateKey;

    /**
     * 路径.
     */
    private String path;

    /**
     * 分支.
     */
    private String branch;

    /**
     * 创建时间.
     */
    private Date gmtCreate;

    /**
     * 修改时间.
     */
    private Date gmtModified;

    /**
     * 同步时间.
     */
    private Date syncTime;

    /**
     * 上次同步的git-commit-id.
     */
    private String commitId;

    /**
     * 高级配置json格式.
     */
    private String advanceConfigure;

    /**
     * 保留原来的密码.
     */
    private Integer passwordKeep;

    /**
     * 保留原来的私钥口令.
     */
    private Integer passphraseKeep;

    /**
     * 保留原来的git私钥.
     */
    private Integer privateKeyKeep;

    /**
     * 登录方式 (1用户名密码；2私钥).
     */
    private Integer authType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getRepoUuid() {
        return repoUuid;
    }

    public void setRepoUuid(String repoUuid) {
        this.repoUuid = repoUuid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getAdvanceConfigure() {
        return advanceConfigure;
    }

    public void setAdvanceConfigure(String advanceConfigure) {
        this.advanceConfigure = advanceConfigure;
    }

    public Integer getPasswordKeep() {
        return passwordKeep;
    }

    public void setPasswordKeep(Integer passwordKeep) {
        this.passwordKeep = passwordKeep;
    }

    public Integer getPassphraseKeep() {
        return passphraseKeep;
    }

    public void setPassphraseKeep(Integer passphraseKeep) {
        this.passphraseKeep = passphraseKeep;
    }

    public Integer getPrivateKeyKeep() {
        return privateKeyKeep;
    }

    public void setPrivateKeyKeep(Integer privateKeyKeep) {
        this.privateKeyKeep = privateKeyKeep;
    }

    public Integer getAuthType() {
        return authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
    }
}