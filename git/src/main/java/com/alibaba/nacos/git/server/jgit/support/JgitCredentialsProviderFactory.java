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

package com.alibaba.nacos.git.server.jgit.support;

import com.alibaba.nacos.git.server.git.support.GitSkipSslValidationCredentialsProvider;
import com.alibaba.nacos.git.server.git.support.PassphraseCredentialsProvider;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.util.StringUtils.hasText;

/**
 * CredentialsProviderFactor for userName & password.
 *
 * @author yueshiqi
 *
 */
public class JgitCredentialsProviderFactory {

    protected static final Logger logger = LoggerFactory.getLogger(JgitCredentialsProviderFactory.class);

    /**
     * Search for a credential provider that will handle the specified URI. If not found,
     * and the username or passphrase has text, then create a default using the provided
     * username and password or passphrase.
     * If skipSslValidation is true and the URI has an https scheme, the default
     * credential provider's behaviour is modified to suppress any SSL validation errors
     * that occur when communicating via the URI.
     * Otherwise null.
     * @param uri uri (cannot be null)
     * @param username username (may be null)
     * @param password password (may be null)
     * @param passphrase passphrase (may be null)
     * @param skipSslValidation skipSslValidation when connecting via HTTPS
     * @return the first matched credentials provider or the default or null.
     */
    public static CredentialsProvider createFor(String uri, String username, String password, String passphrase,
                                         boolean skipSslValidation) {
        CredentialsProvider provider = null;

        if (hasText(passphrase)){
            logger.debug("Constructing PassphraseCredentialsProvider for URI " + uri);
            provider = new PassphraseCredentialsProvider(passphrase);
        } else if(hasText(username)) {
            logger.debug("Constructing UsernamePasswordCredentialsProvider for URI " + uri);
            provider = new UsernamePasswordCredentialsProvider(username, password);
        }

        if (skipSslValidation && GitSkipSslValidationCredentialsProvider.canHandle(uri)) {
            logger.debug("Constructing GitSkipSslValidationCredentialsProvider for URI " + uri);
            provider = new GitSkipSslValidationCredentialsProvider(provider);
        }

        if (provider == null) {
            logger.debug("No credentials provider required for URI " + uri);
        }

        return provider;
    }

}
