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
import org.maxur.ddd.service.components.BusinessException;
import org.maxur.ddd.service.components.NotFoundException;
import org.slf4j.Logger;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * The type Business exception handler.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>11/6/2015</pre>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class BusinessExceptionHandler implements ExceptionMapper<BusinessException> {

    private static final Logger LOGGER = getLogger(BusinessExceptionHandler.class);

    @Override
    public Response toResponse(BusinessException exception) {
        LOGGER.error(exception.getMessage(), exception);
        return Response
                .status(makeStatus(exception))
                .type(APPLICATION_JSON)
                .entity(makeErrorEntity(exception))
                .build();
    }

    @Contract(pure = true)
    private Response.Status makeStatus(BusinessException exception) {
        final Response.Status status;
        if (exception instanceof NotFoundException) {
            status = NOT_FOUND;
        } else {
            status = BAD_REQUEST;
        }
        return status;
    }

    @Contract("_ -> !null")
    private GenericEntity<List<Incident>> makeErrorEntity(final BusinessException exception) {
        return new GenericEntity<List<Incident>>(Incident.incidents("Invalid data", exception.getMessage())) {
        };
    }
}