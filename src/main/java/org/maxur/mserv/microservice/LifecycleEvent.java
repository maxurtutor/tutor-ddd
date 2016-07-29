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
    private final String message;

    /**
     * Instantiates a new Lifecycle event.
     *
     * @param message the message
     */
    protected LifecycleEvent(final String message) {
        this.message = message;
    }
}
