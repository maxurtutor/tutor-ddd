package org.maxur.ddd.infrastructure.view;

import com.codahale.metrics.annotation.Timed;
import org.maxur.ddd.admin.service.AccountService;
import org.maxur.ddd.commons.domain.BusinessException;
import org.maxur.ddd.commons.domain.EmailAddress;
import org.maxur.ddd.commons.domain.Id;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.maxur.ddd.commons.domain.Id.id;
import static org.maxur.ddd.commons.domain.Person.person;


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
    public UserDto add(@Valid UserDto dto) throws BusinessException {
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
    public Boolean changePassword(@PathParam("id") String id, @Valid UserDto dto) throws BusinessException {
        service.changeUserPassword(id(id), dto.password);
        return true;
    }

    @Timed
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean update(@PathParam("id") String id, @Valid UserDto dto) throws BusinessException {
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
