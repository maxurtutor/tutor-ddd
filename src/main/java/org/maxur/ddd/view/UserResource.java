package org.maxur.ddd.view;

import com.codahale.metrics.annotation.Timed;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.domain.UserRepository;
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

    private final UserRepository repository;

    private final CommandHandler commandHandler;

    @Inject
    public UserResource(AccountService service, UserRepository repository, CommandHandler commandHandler) {
        this.service = service;
        this.repository = repository;
        this.commandHandler = commandHandler;
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
    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public User setPassword(@PathParam("id") String userId, String password) throws BusinessException {
        Command<User> command = new ChangePasswordCommand(userId, password);
        return commandHandler.handle(command);
    }

    @Timed
    @GET
    @Path("/{id}")
    public User find(@PathParam("id") String id) throws NotFoundException {
        return repository.findById(id);
    }

    @Timed
    @GET
    @Path("/")
    public List<User> findAll() {
        return repository.findAll();
    }

}
