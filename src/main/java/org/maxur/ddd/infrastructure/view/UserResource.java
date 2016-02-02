package org.maxur.ddd.infrastructure.view;

import com.codahale.metrics.annotation.Timed;
import org.maxur.ddd.domain.BusinessException;
import org.maxur.ddd.domain.EmailAddress;
import org.maxur.ddd.domain.Id;
import org.maxur.ddd.account.service.AccountService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.maxur.ddd.domain.Id.id;
import static org.maxur.ddd.domain.Person.person;

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
    public UserDto add(UserDto dto) throws BusinessException {
        return UserDto.from(
                service.createUserBy(
                        dto.name,
                        person(dto.firstName, dto.lastName, EmailAddress.email(dto.email)), id(dto.teamId)
                )
        );
    }

    @Timed
    @PUT
    @Path("/{id}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean changePassword(@PathParam("id") String id, UserDto dto) throws BusinessException {
        service.changeUserPassword(id(id), dto.password);
        return true;
    }

    @Timed
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean update(@PathParam("id") String id, UserDto dto) throws BusinessException {
        service.changeUserInfo(
                id(id), person(dto.firstName, dto.lastName, EmailAddress.email(dto.email)), Id.id(dto.teamId)
        );
        return true;
    }

    @Timed
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean delete(@PathParam("id") String id) throws BusinessException {
        service.deleteUserBy(id(id));
        return true;
    }

    @Timed
    @GET
    @Path("/{id}")
    public UserDto find(@PathParam("id") String id) throws BusinessException {
        return UserDto.from(service.findUserById(id(id)));
    }

    @Timed
    @GET
    @Path("/")
    public List<UserDto> findAll() {
        return service.findAllUsers().stream().map(UserDto::from).collect(toList());
    }

}
