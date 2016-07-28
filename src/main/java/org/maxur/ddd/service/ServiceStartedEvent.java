package org.maxur.ddd.service;

import org.maxur.ddd.domain.Event;

/**
 * The type Application started event.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public final class ServiceStartedEvent extends Event {

    private ServiceStartedEvent() {
    }

    /**
     * Application started event.
     *
     * @return the event
     */
    public static Event serviceStartedEvent() {
        return new ServiceStartedEvent();
    }

}
