package org.maxur.ddd.infrastructure.view;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.*;
import org.maxur.ddd.admin.service.AccountService;
import org.maxur.ddd.commons.domain.BusinessException;
import org.maxur.ddd.commons.domain.EmailAddress;
import org.maxur.ddd.commons.domain.Id;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
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
@Api(value = "/users", description = "Operations on or related to users.")
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
    @ApiOperation(
            value = "Gets the user's details to display on the page.",
            response = UserDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Bad auth on user.")
    })
    public UserDto find(
            @ApiParam @PathParam("id")
                    String id
    ) throws BusinessException {
        return UserDto.from(service.findUserById(id(id)));
    }

    @Timed
    @GET
    @Path("/me")
    public UserDto findCurrentUser() throws BusinessException {
        return null;
    }

    @Timed
    @GET
    @Path("/")
    public List<UserDto> findAll() {
        return service.findAllUsers().stream().map(UserDto::from).collect(toList());
    }

}
