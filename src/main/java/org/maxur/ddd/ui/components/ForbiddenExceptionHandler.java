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

package org.maxur.ddd.ui.components;

import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * The type Forbidden exception handler.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>11/6/2015</pre>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ForbiddenExceptionHandler implements ExceptionMapper<ForbiddenException> {

    private static final Logger LOGGER = getLogger(ForbiddenExceptionHandler.class);

    @Override
    public Response toResponse(ForbiddenException exception) {
        LOGGER.error(exception.getMessage(), exception);
        return Response
                .status(Response.Status.FORBIDDEN)
                .type(APPLICATION_JSON)
                .entity(makeErrorEntity(exception))
                .build();
    }

    @Contract("_ -> !null")
    private static GenericEntity<List<Incident>> makeErrorEntity(final ForbiddenException exception) {
        return new GenericEntity<List<Incident>>(Incident.incidents("Forbidden", exception.getMessage())) {
        };
    }
}