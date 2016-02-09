package org.maxur.ddd.ui.components;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.maxur.ddd.dao.UserDao;
import org.maxur.ddd.domain.User;

import javax.inject.Inject;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/5/2016</pre>
 */
public class BaseAuthenticator implements Authenticator<BasicCredentials, UserPrincipal> {

    private final UserDao dao;

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
