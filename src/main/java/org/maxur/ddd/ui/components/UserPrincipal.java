package org.maxur.ddd.ui.components;

import org.maxur.ddd.domain.User;

import java.security.Principal;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/5/2016</pre>
 */
public class UserPrincipal implements Principal {

    private final User user;

    UserPrincipal(User user) {
        this.user = user;
    }

    UserPrincipal(String token) {
        this.user = new User(token, token, "");
        user.addRole("ADMIN");
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

}
