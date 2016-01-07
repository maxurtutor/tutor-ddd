package org.maxur.ddd.view;

import com.codahale.metrics.annotation.Timed;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.service.*;
import org.maxur.ddd.service.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public User add(User user) throws BusinessException {
        service.create(user);
        return user;
    }

    @Timed
    @GET
    @Path("/{id}")
    public User find(@PathParam("id") String id) throws NotFoundException {
        return service.findById(id);
    }

    @Timed
    @GET
    @Path("/")
    public List<User> findAll() {
        return service.findAll();
    }

}
