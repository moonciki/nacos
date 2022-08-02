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
 * git repo commit status.
 *
 * @author ysq
 * @date 2022/7/20 14:35
 */
public class GitCommitStatus {

    /* ============================== git info ============================== */

    /**
     * git uri.
     */
    private String uri;

    /**
     * 同步时间.
     */
    private Date syncTime;

    /**
     * 上次同步的git-commit-id.
     */
    private String dbCommitId;

    /* ============================== commit info ============================== */
    /**
     * 提交号.
     */
    private String remoteCommitId;

    /**
     * 作者.
     */
    private String author;

    /**
     * 备注.
     */
    private String message;

    /**
     * 创建日期.
     */
    private Date commitDate;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    public String getDbCommitId() {
        return dbCommitId;
    }

    public void setDbCommitId(String dbCommitId) {
        this.dbCommitId = dbCommitId;
    }

    public String getRemoteCommitId() {
        return remoteCommitId;
    }

    public void setRemoteCommitId(String remoteCommitId) {
        this.remoteCommitId = remoteCommitId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

}
