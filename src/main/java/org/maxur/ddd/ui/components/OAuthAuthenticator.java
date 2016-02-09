package org.maxur.ddd.ui.components;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.maxur.ddd.config.Config;

import java.util.List;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/5/2016</pre>
 */
public class OAuthAuthenticator implements Authenticator<String, UserPrincipal> {

    private final List<String> tokens;

    public OAuthAuthenticator(Config.Security security) {
        tokens = security.getTokens();
    }

    @Override
    public Optional<UserPrincipal> authenticate(String token) throws AuthenticationException {
        return tokens.contains(token) ?
            Optional.of(new UserPrincipal(token)) :
            Optional.absent();
    }

}
