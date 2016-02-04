package org.maxur.ddd.ui;

import com.codahale.metrics.annotation.Timed;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.service.AccountService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    public User find(@PathParam("id") String id) {
        return service.getCurrentUser();
    }

}
