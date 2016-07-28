package org.maxur.ddd.service;

import org.maxur.ddd.domain.Event;

/**
 * The type Application stopped event.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public final class ServiceStoppedEvent extends Event {

    private ServiceStoppedEvent() {
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
