/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.git.server.git.env;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.git.server.model.TenantGit;

import java.util.HashMap;
import java.util.Map;

/**
 * jgit advinceConfigure.
 * @author Dylan Roberts
 * @author Gareth Clay
 * @author Shiqi Yue
 */
public class GitEnvironmentProperties {

    @Deprecated
    private String uri;

    /**
     * Timeout (in seconds) for obtaining HTTP or SSH connection (if applicable), defaults
     * to 5 seconds.
     */
    private int timeout = 5;

    /**
     * Flag to indicate that SSL certificate validation should be bypassed when
     * communicating with a repository served over an HTTPS connection.
     */
    private boolean skipSslValidation = false;

    /**
     * Valid SSH private key. Must be set if ignoreLocalSshSettings is true and Git URI is
     * SSH format.
     */
    private String privateKey;

    /**
     * ssh passphrase.
     */
    private String passphrase;

    /**
     * One of ssh-dss, ssh-rsa, ecdsa-sha2-nistp256, ecdsa-sha2-nistp384, or
     * ecdsa-sha2-nistp521. Must be set if hostKey is also set.
     */
    private String hostKeyAlgorithm;

    /**
     * Valid SSH host key. Must be set if hostKeyAlgorithm is also set.
     */
    private String hostKey;

    /**
     * Location of custom .known_hosts file.
     */
    private String knownHostsFile;

    /**
     * Override server authentication method order. This should allow for evading login
     * prompts if server has keyboard-interactive authentication before the publickey
     * method.
     * eg. "publickey,keyboard-interactive,password" .
     * eg. @Pattern(regexp = "([\\w -]+,)*([\\w -]+)").
     */
    private String preferredAuthentications;

    /**
     * If false, ignore errors with host key.
     */
    private boolean strictHostKeyChecking = false;

    /**
     * default protocol.version .
     */
    private int protocolVersion = 1;

    /**
     * HTTP proxy configuration.
     */
    private Map<ProxyHostProperties.ProxyForScheme, ProxyHostProperties> proxy = new HashMap<>();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getHostKeyAlgorithm() {
        return this.hostKeyAlgorithm;
    }

    public void setHostKeyAlgorithm(String hostKeyAlgorithm) {
        this.hostKeyAlgorithm = hostKeyAlgorithm;
    }

    public String getHostKey() {
        return this.hostKey;
    }

    public void setHostKey(String hostKey) {
        this.hostKey = hostKey;
    }

    public String getKnownHostsFile() {
        return this.knownHostsFile;
    }

    public void setKnownHostsFile(String knownHostsFile) {
        this.knownHostsFile = knownHostsFile;
    }

    public String getPreferredAuthentications() {
        return this.preferredAuthentications;
    }

    public void setPreferredAuthentications(String preferredAuthentications) {
        this.preferredAuthentications = preferredAuthentications;
    }

    public boolean isStrictHostKeyChecking() {
        return this.strictHostKeyChecking;
    }

    public void setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        this.strictHostKeyChecking = strictHostKeyChecking;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public boolean isSkipSslValidation() {
        return this.skipSslValidation;
    }

    public void setSkipSslValidation(boolean skipSslValidation) {
        this.skipSslValidation = skipSslValidation;
    }

    public Map<ProxyHostProperties.ProxyForScheme, ProxyHostProperties> getProxy() {
        return this.proxy;
    }

    public void setProxy(Map<ProxyHostProperties.ProxyForScheme, ProxyHostProperties> proxy) {
        this.proxy = proxy;
    }

    /**
     * load git advanceConfigure properties.
     * @param tenantGit tenantGit
     * @return JGitEnvironmentProperties
     */
    public static GitEnvironmentProperties loadProperties(TenantGit tenantGit) {

        String advanceConfigure = tenantGit.getAdvanceConfigure();

        GitEnvironmentProperties jgitEnv = null;

        if (StringUtils.isBlank(advanceConfigure)) {
            jgitEnv = new GitEnvironmentProperties();
        } else {
            jgitEnv = JacksonUtils.toObj(advanceConfigure, GitEnvironmentProperties.class);
        }

        jgitEnv.setPrivateKey(tenantGit.getPrivateKey());
        jgitEnv.setPassphrase(tenantGit.getPassphrase());
        //jgitEnv.setHostKeyAlgorithm("ssh-rsa");

        return jgitEnv;
    }

}
