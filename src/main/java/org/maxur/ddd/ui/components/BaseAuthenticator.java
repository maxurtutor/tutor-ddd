package org.maxur.ddd.ui.components;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.service.AccountService;

import javax.inject.Inject;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/5/2016</pre>
 */
public class BaseAuthenticator implements Authenticator<BasicCredentials, UserPrincipal> {

    private final AccountService service;

    @Inject
    public BaseAuthenticator(AccountService service) {
        this.service = service;
    }

    @SuppressWarnings("Guava")
    @Override
    public Optional<UserPrincipal> authenticate(BasicCredentials credentials) throws AuthenticationException {
        User user = service.findUserByEmail(credentials.getUsername());
        if (user.getPassword().equals(credentials.getPassword())) {
            return Optional.of(new UserPrincipal(user));
        }
        return Optional.absent();
    }
}
