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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.maxur.ddd.domain.User;

import java.security.Principal;

/**
 * The type User principal.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/5/2016</pre>
 */
public class UserPrincipal implements Principal {

    private final User user;

    /**
     * Instantiates a new User principal.
     *
     * @param user the user
     */
    UserPrincipal(@NotNull User user) {
        this.user = user;
    }

    /**
     * Instantiates a new User principal.
     *
     * @param token the token
     */
    UserPrincipal(@NotNull String token) {
        this.user = new User(token, token, "");
        user.addRole("ADMIN");
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    @Contract(" -> !null")
    public User getUser() {
        return user;
    }

    @Override
    @Contract(" -> !null")
    public String getName() {
        return user.getEmail();
    }

}
