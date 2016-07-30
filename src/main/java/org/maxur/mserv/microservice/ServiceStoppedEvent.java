package org.maxur.mserv.microservice;

import org.maxur.ddd.domain.Event;

import static java.lang.String.format;

/**
 * The type Application stopped event.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public final class ServiceStoppedEvent extends LifecycleEvent {

    private ServiceStoppedEvent(final MicroService service) {
        super(service);
    }

    /**
     * Application stopped event.
     *
     * @return the event
     * @param service the MicroService
     */
    public static Event serviceStoppedEvent(final MicroService service) {
        return new ServiceStoppedEvent(service);
    }

    @Override
    public String message() {
        return format("Service '%s' is stopped",
                getService().getName()
        );
    }

}
