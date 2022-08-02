/*
 * Copyright 2013-2019 the original author or authors.
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

package com.alibaba.nacos.git.server.git.support;

import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.git.server.git.env.GitEnvironmentProperties;
import com.alibaba.nacos.git.server.git.ssh.PropertiesBasedSshTransportConfigCallback;
import org.eclipse.jgit.api.TransportConfigCallback;

/**
 * config 回调.
 * @author yueshiqi
 */
public class TransportConfigCallbackFactory {

    /**
     * build TransportConfigCallback.
     * @param environmentProperties environmentProperties
     * @return TransportConfigCallback
     */
    public static TransportConfigCallback build(GitEnvironmentProperties environmentProperties) {

        // If the currently configured repository is a Google Cloud Source repository
        // we use GoogleCloudSourceSupport.
        /*
        if (googleCloudSourceSupport != null) {
            final String uri = environmentProperties.getUri();
            if (googleCloudSourceSupport.canHandle(uri)) {
                return googleCloudSourceSupport.createTransportConfigCallback();
            }
        }
        */

        // Otherwise - legacy behaviour - use SshTransportConfigCallback for all
        // repositories.
        TransportConfigCallback transportConfigCallback = buildSshTransportConfigCallback(environmentProperties);

        return transportConfigCallback;
    }

    private static TransportConfigCallback buildSshTransportConfigCallback(GitEnvironmentProperties gitEnvironmentProperties) {

        if (gitEnvironmentProperties == null) {
            return null;
        }

        TransportConfigCallback transportConfigCallback = null;

        if (StringUtils.isNotBlank(gitEnvironmentProperties.getPrivateKey())) {
            transportConfigCallback = new PropertiesBasedSshTransportConfigCallback(gitEnvironmentProperties);
        }

        //暂不支持本地ssh key 读取
        //return new FileBasedSshTransportConfigCallback(gitEnvironmentProperties);

        return transportConfigCallback;
    }

}
