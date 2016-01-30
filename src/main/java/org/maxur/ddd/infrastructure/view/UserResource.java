package org.maxur.ddd.infrastructure.view;

import com.codahale.metrics.annotation.Timed;
import org.maxur.ddd.domain.BusinessException;
import org.maxur.ddd.service.AccountService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

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
        return UserDto.from(service.create(dto.assemble()));
    }

    @Timed
    @PUT
    @Path("/{id}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean changePassword(@PathParam("id") String id, UserDto dto) throws BusinessException {
        service.changePassword(id, dto.password);
        return true;
    }

    @Timed
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean update(@PathParam("id") String id, UserDto dto) throws BusinessException {
        service.update(dto.assemble());
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
    public UserDto find(@PathParam("id") String id) throws BusinessException {
        return UserDto.from(service.findById(id));
    }

    @Timed
    @GET
    @Path("/")
    public List<UserDto> findAll() {
        return service.findAll().stream().map(UserDto::from).collect(toList());
    }

}
