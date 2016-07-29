package org.maxur.mserv.microservice;

import org.maxur.ddd.domain.Event;

/**
 * The type Application stopped event.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public final class ServiceStoppedEvent extends LifecycleEvent {

    private ServiceStoppedEvent() {
        super("Service is stopped");
    }

    /**
     * Application stopped event.
     *
     * @return the event
     */
    public static Event serviceStoppedEvent() {
        return new ServiceStoppedEvent();
    }

}
