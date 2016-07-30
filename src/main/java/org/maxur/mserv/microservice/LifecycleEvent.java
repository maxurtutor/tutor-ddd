package org.maxur.mserv.microservice;

import lombok.Getter;
import org.maxur.ddd.domain.Event;

/**
 * The type Lifecycle event.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/29/2016</pre>
 */
public abstract class LifecycleEvent extends Event {

    @Getter
    private final MicroService service;

    /**
     * Instantiates a new Lifecycle event.
     *
     * @param service the service
     */
    LifecycleEvent(final MicroService service) {

        this.service = service;
    }

    /**
     * Message string.
     *
     * @return the string
     */
    public abstract String message();
}
