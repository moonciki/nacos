/*
 * Copyright 2015-2019 the original author or authors.
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

package com.alibaba.nacos.git.server.git.ssh;

import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.git.server.git.env.GitEnvironmentProperties;
import com.alibaba.nacos.git.server.git.env.ProxyHostProperties;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.util.FS;

/**
 * In a cloud environment local SSH config files such as `.known_hosts` may not be
 * suitable for providing configuration settings due to ephemeral filesystems. This flag
 * enables SSH config to be provided as application properties
 *
 * @author William Tran
 * @author Ollie Hughes
 */
public class PropertyBasedSshSessionFactory extends JschConfigSessionFactory {

    private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";

    private static final String PREFERRED_AUTHENTICATIONS = "PreferredAuthentications";

    private static final String YES_OPTION = "yes";

    private static final String NO_OPTION = "no";

    private static final String SERVER_HOST_KEY = "server_host_key";

    private GitEnvironmentProperties jgitEnvironmentProperties;

    private final JSch jSch;

    public PropertyBasedSshSessionFactory(GitEnvironmentProperties jgitEnvironmentProperties, JSch jSch) {
        this.jgitEnvironmentProperties = jgitEnvironmentProperties;
        this.jSch = jSch;
    }

    @Override
    protected void configure(OpenSshConfig.Host hc, Session session) {

        session.setConfig("protocol.version", "0");

        String hostKeyAlgorithm = jgitEnvironmentProperties.getHostKeyAlgorithm();
        if (hostKeyAlgorithm != null) {
            session.setConfig(SERVER_HOST_KEY, hostKeyAlgorithm);
        }
        if (jgitEnvironmentProperties.getHostKey() == null || !jgitEnvironmentProperties.isStrictHostKeyChecking()) {
            session.setConfig(STRICT_HOST_KEY_CHECKING, NO_OPTION);
        } else {
            session.setConfig(STRICT_HOST_KEY_CHECKING, YES_OPTION);
        }
        String preferredAuthentications = jgitEnvironmentProperties.getPreferredAuthentications();
        if (preferredAuthentications != null) {
            session.setConfig(PREFERRED_AUTHENTICATIONS, preferredAuthentications);
        }

        ProxyHostProperties proxyHostProperties = jgitEnvironmentProperties.getProxy().get(ProxyHostProperties.ProxyForScheme.HTTP);
        if (proxyHostProperties != null) {
            ProxyHTTP proxy = createProxy(proxyHostProperties);
            proxy.setUserPasswd(proxyHostProperties.getUsername(), proxyHostProperties.getPassword());
            session.setProxy(proxy);
        }
    }

    protected ProxyHTTP createProxy(ProxyHostProperties proxyHostProperties) {
        return new ProxyHTTP(proxyHostProperties.getHost(), proxyHostProperties.getPort());
    }

    @Override
    protected Session createSession(OpenSshConfig.Host hc, String user, String host, int port, FS fs) throws JSchException {

        byte[] privateKeyBytes = jgitEnvironmentProperties.getPrivateKey().getBytes();

        byte[] passphraseBytes = null;

        String passphrase = jgitEnvironmentProperties.getPassphrase();

        if (StringUtils.isNotBlank(passphrase)) {
            passphraseBytes = passphrase.getBytes();
        }

        this.jSch.addIdentity(host, privateKeyBytes, null, passphraseBytes);
        if (jgitEnvironmentProperties.getKnownHostsFile() != null) {
            this.jSch.setKnownHosts(jgitEnvironmentProperties.getKnownHostsFile());
        }

        return this.jSch.getSession(user, host, port);

    }

}
