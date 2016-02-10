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

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.maxur.ddd.dao.UserDao;
import org.maxur.ddd.domain.User;

import javax.inject.Inject;

/**
 * The type Base authenticator.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/5/2016</pre>
 */
public class BaseAuthenticator implements Authenticator<BasicCredentials, UserPrincipal> {

    private final UserDao dao;

    /**
     * Instantiates a new Base authenticator.
     *
     * @param dao the dao
     */
    @Inject
    public BaseAuthenticator(UserDao dao) {
        this.dao = dao;
    }

    @SuppressWarnings("Guava")
    @Override
    public Optional<UserPrincipal> authenticate(BasicCredentials credentials) throws AuthenticationException {
        User user = dao.findByEmail(credentials.getUsername());
        if (user != null &&  user.getPassword().equals(credentials.getPassword())) {
            return Optional.of(new UserPrincipal(user));
        }
        return Optional.absent();
    }
}
