package org.maxur.ddd.ui;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.service.AccountService;
import org.maxur.ddd.ui.components.UserPrincipal;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.11.2015</pre>
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final AccountService service;

    @Inject
    public UserResource(AccountService service) {
        this.service = service;
    }

    @Timed
    @GET
    @Path("/me")
    public User currentUser(@Auth UserPrincipal principal) {
        return principal.getUser();
    }


    @Timed
    @GET
    @Path("/")
    @RolesAllowed("ADMIN")
    public Collection<User> findAll() {
        return service.findAll();
    }

    @Timed
    @GET
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public User findById(@PathParam("id") String id) {
        return service.findById(id);
    }

}
