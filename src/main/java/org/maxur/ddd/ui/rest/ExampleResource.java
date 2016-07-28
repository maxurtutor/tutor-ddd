package org.maxur.ddd.ui.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Alexey Elin
 * @version 1.0 18.12.2015.
 */
@Path("/api/v1.0")
@Singleton
@Api(tags = "Example Resource")
public class ExampleResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleResource.class);

    @SuppressWarnings("unused")
    @Named("param")
    private String configParam;

    @GET
    @Path("/hello")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "Description is here"
    )
    public Response doHello() {
        LOGGER.info("example resource: /hello");
        return Response.ok("Hello World!").build();
    }

    @GET
    @Path("/config")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "Description is here"
    )
    public Response doConfigParam() {
        LOGGER.info("example resource: /config");
        return Response.ok(configParam).build();
    }
}
