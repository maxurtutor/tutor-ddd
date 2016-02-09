package org.maxur.ddd.ui.components;

import io.dropwizard.auth.Authorizer;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/5/2016</pre>
 */
public class BaseAuthorizer implements Authorizer<UserPrincipal> {

    @Override
    public boolean authorize(UserPrincipal principal, String role) {

        return principal.getUser().is(role);
    }

}
