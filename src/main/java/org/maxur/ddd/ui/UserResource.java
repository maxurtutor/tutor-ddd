/*
 * Copyright (c) 2016 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.maxur.ddd.ui;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.service.AccountService;
import org.maxur.ddd.service.components.NotFoundException;
import org.maxur.ddd.ui.components.UserPrincipal;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

/**
 * The type User resource. Operations on or related to system users.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>12.11.2015</pre>
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/users", description = "Operations on or related to system users.")
public class UserResource {

    private final AccountService service;

    /**
     * Instantiates a new User resource.
     *
     * @param service the service
     */
    @Inject
    public UserResource(AccountService service) {
        this.service = service;
    }

    /**
     * Current user.
     *
     * @param principal the principal
     * @return the user
     */
    @Timed
    @GET
    @Path("/me")
    @ApiOperation(
        value = "Gets current user.",
        response = User.class
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "System Error")
    })
    public User currentUser(@Auth UserPrincipal principal) {
        return principal.getUser();
    }


    /**
     * Find all Users.
     *
     * @return the collection of User
     */
    @Timed
    @GET
    @Path("/")
    @RolesAllowed("ADMIN")
    @ApiOperation(
        value = "Gets all users.",
        response = User.class,
        responseContainer = "List"

    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 500, message = "System Error")
    })
    public Collection<User> findAll() {
        return service.findAll();
    }

    /**
     * Find user by id.
     *
     * @param id the id
     * @return the user
     * @throws NotFoundException the not found exception
     */
    @Timed
    @GET
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @ApiOperation(
        value = "Gets the user's details.",
        response = User.class
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 500, message = "System Error")
    })
    public User findById(
        @ApiParam(value = "User's identifier", required = true)
        @PathParam("id")
        String id
    )
        throws NotFoundException {
        return service.findById(id);
    }

}
