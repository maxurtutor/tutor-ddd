package org.maxur.mserv.microservice;

import java.text.SimpleDateFormat;

import static java.lang.String.format;

/**
 * The type Application started event.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public final class ServiceStartedEvent extends LifecycleEvent {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private ServiceStartedEvent(final MicroService service) {
        super(service);
    }

    /**
     * Application started event.
     *
     * @return the event
     * @param service the MicroService
     */
    public static ServiceStartedEvent serviceStartedEvent(final MicroService service) {
        return new ServiceStartedEvent(service);
    }

    @Override
    public String message() {
        return format("Service %s v.%s (%s) is started",
                getService().getName(),
                getService().getVersion(),
                dateFormat.format(getService().getReleased())
        );
    }
}
