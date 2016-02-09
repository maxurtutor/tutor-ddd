package org.maxur.ddd.ui.components;

import org.slf4j.Logger;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>11/6/2015</pre>
 */
@SuppressWarnings("unused")
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

    private GenericEntity<List<Incident>> makeErrorEntity(final ForbiddenException exception) {
        return new GenericEntity<List<Incident>>(Incident.incidents("Forbidden", exception.getMessage())) {
        };
    }
}