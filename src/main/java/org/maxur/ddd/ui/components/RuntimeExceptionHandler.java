package org.maxur.ddd.ui.components;

import org.slf4j.Logger;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.maxur.ddd.ui.components.Incident.incidents;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>11/6/2015</pre>
 */
@SuppressWarnings("unused")
public class RuntimeExceptionHandler implements ExceptionMapper<RuntimeException> {

    private static final Logger LOGGER = getLogger(RuntimeExceptionHandler.class);

    @Override
    public Response toResponse(RuntimeException exception) {
        LOGGER.error(exception.getMessage(), exception);
        return Response
                .status(INTERNAL_SERVER_ERROR)
                .type(APPLICATION_JSON)
                .entity(makeErrorEntity(exception))
                .build();
    }

    private GenericEntity<List<Incident>> makeErrorEntity(final RuntimeException exception) {
        return new GenericEntity<List<Incident>>(
                incidents("System error: For more information consult your system administrator.")
        ) {
        };
    }


}