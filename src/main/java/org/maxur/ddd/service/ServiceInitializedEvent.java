package org.maxur.ddd.service;

import org.maxur.ddd.domain.Event;

/**
 * The type Application initialized event.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public final class ServiceInitializedEvent extends Event {

    private ServiceInitializedEvent() {
    }

    /**
     * Application initialized event.
     *
     * @return the event
     */
    public static Event serviceInitializedEvent() {
        return new ServiceInitializedEvent();
    }

}
