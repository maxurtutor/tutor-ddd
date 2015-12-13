package org.maxur.ddd.view;

import com.codahale.metrics.annotation.Timed;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.domain.ValidationException;
import org.maxur.ddd.service.*;
import org.maxur.ddd.domain.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.11.2015</pre>
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final AccountService service;

    @Inject
    public UserResource(AccountService service) {
        this.service = service;
    }

    @Timed
    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    public UserView add(AddUserCommand command) throws ValidationException {
        User user = command.assemble();
        service.create(user);
        return UserView.view(user);
    }

    @Timed
    @GET
    @Path("/user/{id}")
    public UserView find(@PathParam("id") String id) throws ValidationException {
        return UserView.view(service.findById(id));
    }

    @Timed
    @GET
    @Path("/users")
    public List<UserView> findAll() throws ValidationException {
        return service.findAll().stream().map(UserView::view).collect(Collectors.toList());
    }

}
