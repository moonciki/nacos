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

package com.alibaba.nacos.git.server.git.ssh;

import com.alibaba.nacos.git.server.git.env.GitEnvironmentProperties;
import com.jcraft.jsch.JSch;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

/**
 * Configure JGit transport command to use a SSH session factory that is configured using
 * properties defined in {@link GitEnvironmentProperties}.
 *
 * @author Dylan Roberts
 * @author Shiqi Yue
 */
public class PropertiesBasedSshTransportConfigCallback implements TransportConfigCallback {

    private GitEnvironmentProperties sshUriProperties;

    public PropertiesBasedSshTransportConfigCallback(GitEnvironmentProperties sshUriProperties) {
        this.sshUriProperties = sshUriProperties;
    }

    @Override
    public void configure(Transport transport) {
        if (transport instanceof SshTransport) {
            SshTransport sshTransport = (SshTransport) transport;
            //sshTransport.setCredentialsProvider(new CustomCredentialProvider(passphrase));

            //sshTransport.setSshSessionFactory(new SshdSessionFactory());
            sshTransport.setSshSessionFactory(new PropertyBasedSshSessionFactory(sshUriProperties, new JSch()));
        }
    }

}
