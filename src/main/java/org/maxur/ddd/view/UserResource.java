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
 */ // The actual service
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
    public User add(UserDTO dto) throws ValidationException {
        User user = dto.assemble();
        service.create(user);
        return user;
    }

    @Timed
    @GET
    @Path("/user/{id}")
    public UserDTO find(@PathParam("id") String id) throws NotFoundException {
        return UserDTO.dto(service.findById(id));
    }

    @Timed
    @GET
    @Path("/users")
    public List<UserDTO> findAll() {
        return service.findAll().stream().map(UserDTO::dto).collect(Collectors.toList());
    }

}
