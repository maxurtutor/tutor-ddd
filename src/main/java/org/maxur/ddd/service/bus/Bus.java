package org.maxur.ddd.service.bus;

import org.maxur.ddd.domain.Event;

/**
 * The interface Bus.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public interface Bus {

    /**
     * Unregister.
     *
     * @param object the object
     */
    void unregister(Object object);

    /**
     * Post.
     *
     * @param event the event
     */
    void post(Event event);

    /**
     * Register.
     *
     * @param object the object
     */
    void register(Object object);

    /**
     * Id string.
     *
     * @return the string
     */
    String id();

}
