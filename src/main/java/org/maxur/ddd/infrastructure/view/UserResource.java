package org.maxur.ddd.infrastructure.view;

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
    @PUT
    @Path("/{id}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean changePassword(@PathParam("id") String id, User user) throws BusinessException {
        service.changePassword(id, user.getPassword());
        return true;
    }

    @Timed
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean update(@PathParam("id") String id, User user) throws BusinessException {
        service.update(user);
        return true;
    }

    @Timed
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean delete(@PathParam("id") String id) throws BusinessException {
        service.delete(id);
        return true;
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
