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
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import static com.google.common.io.Resources.getResource;
import static java.lang.String.format;

/**
 * The type Application resource. Operations on or related to this Application.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>11/29/13</pre>
 */
@Path("/application")
@Api(value = "/application", description = "Operations on or related to this Application")
public class ApplicationResource {

    /**
     * Gets a application documentation
     *
     * @param uriInfo the uri info
     * @return response ok
     */
    @Timed
    @GET
    @Path("/swagger.json")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "Gets a REST interface documentation.",
        response = String.class
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "System Error")
    })
    public String getJsonDoc(@Context UriInfo uriInfo) {
        final URI uri = uriInfo.getBaseUri();
        final String baseUri = format("%s:%d", uri.getHost(), uri.getPort());
        final URL url = getResource("swagger.json");
        try {
            final String jsonDoc = Resources.toString(url, Charsets.UTF_8);
            return jsonDoc.replaceAll("baseurl", baseUri);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}