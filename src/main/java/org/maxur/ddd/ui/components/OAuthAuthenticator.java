/*
 * Copyright (c) 2016 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.maxur.ddd.ui.components;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.jetbrains.annotations.NotNull;
import org.maxur.ddd.config.Config;

import java.util.List;
import java.util.Optional;

/**
 * The type O auth authenticator.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/5/2016</pre>
 */
public class OAuthAuthenticator implements Authenticator<String, UserPrincipal> {

    private final List<String> tokens;

    /**
     * Instantiates a new O auth authenticator.
     *
     * @param security the security
     */
    public OAuthAuthenticator(Config.Security security) {
        tokens = security.getTokens();
    }

    @Override
    public Optional<UserPrincipal> authenticate(@NotNull String token) throws AuthenticationException {
        return tokens.contains(token) ?
            Optional.of(new UserPrincipal(token)) :
            Optional.empty();
    }

}
