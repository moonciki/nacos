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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import static org.springframework.util.StringUtils.hasText;

/**
 * A CredentialsProvider factory for Git repositories. Can handle AWS CodeCommit
 * repositories and other repositories with username/password.
 *
 * @author Don Laidlaw
 * @author Gareth Clay
 * @author Shiqi Yue
 *
 */
public class GitCredentialsProviderFactory {

    protected Log logger = LogFactory.getLog(getClass());

    /**
     * Search for a credential provider that will handle the specified URI. If not found,
     * and the username or passphrase has text, then create a default using the provided
     * username and password or passphrase.
     * If skipSslValidation is true and the URI has an https scheme, the default
     * credential provider's behaviour is modified to suppress any SSL validation errors
     * that occur when communicating via the URI.
     * Otherwise null.
     * @param uri the URI of the repository (cannot be null)
     * @param username the username provided for the repository (may be null)
     * @param password the password provided for the repository (may be null)
     * @param passphrase the passphrase to unlock the ssh private key (may be null)
     * @param skipSslValidation whether to skip SSL validation when connecting via HTTPS
     * @return the first matched credentials provider or the default or null.
     */
    public CredentialsProvider createFor(String uri, String username, String password, String passphrase,
                                         boolean skipSslValidation) {
        CredentialsProvider provider = null;

        if (hasText(username)) {
            this.logger.debug("Constructing UsernamePasswordCredentialsProvider for URI " + uri);
            provider = new UsernamePasswordCredentialsProvider(username, password.toCharArray());
        } else if (hasText(username) && !hasText(passphrase)) {
            // useful for token based login gh-1602
            // see
            // https://stackoverflow.com/questions/28073266/how-to-use-jgit-to-push-changes-to-remote-with-oauth-access-token
            this.logger.debug("Constructing UsernamePasswordCredentialsProvider for URI " + uri);
            provider = new UsernamePasswordCredentialsProvider(username, (String) null);
        } else if (hasText(passphrase)) {
            this.logger.debug("Constructing PassphraseCredentialsProvider for URI " + uri);
            provider = new PassphraseCredentialsProvider(passphrase);
        }

        if (skipSslValidation && GitSkipSslValidationCredentialsProvider.canHandle(uri)) {
            this.logger.debug("Constructing GitSkipSslValidationCredentialsProvider for URI " + uri);
            provider = new GitSkipSslValidationCredentialsProvider(provider);
        }

        if (provider == null) {
            this.logger.debug("No credentials provider required for URI " + uri);
        }

        return provider;
    }

}
