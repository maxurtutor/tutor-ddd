package org.maxur.ddd.view;

import org.maxur.ddd.service.NotFoundException;
import org.maxur.ddd.service.ValidationException;
import org.slf4j.Logger;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.maxur.ddd.view.Incident.incidents;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>11/6/2015</pre>
 */
public class ValidationExceptionHandler implements ExceptionMapper<ValidationException> {

    private static final Logger LOGGER = getLogger(ValidationExceptionHandler.class);

    @Override
    public Response toResponse(ValidationException exception) {
        LOGGER.error(exception.getMessage(), exception);
        return Response
                .status(makeStatus(exception))
                .type(APPLICATION_JSON)
                .entity(makeErrorEntity(exception))
                .build();
    }

    private Response.Status makeStatus(ValidationException exception) {
        final Response.Status status;
        if (exception instanceof NotFoundException) {
            status = NOT_FOUND;
        } else {
            status = BAD_REQUEST;
        }
        return status;
    }

    private GenericEntity<List<Incident>> makeErrorEntity(final ValidationException exception) {
        return new GenericEntity<List<Incident>>(incidents("Invalid data", exception.getMessage())) {
        };
    }
}