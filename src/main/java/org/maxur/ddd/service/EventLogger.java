package org.maxur.ddd.service;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;
import org.maxur.mserv.bus.Bus;
import org.maxur.mserv.microservice.LifecycleEvent;
import org.maxur.mserv.microservice.Observer;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * The type Logger.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
@Slf4j
@Observer
@Service
public class EventLogger {

    /**
     * Instantiates a new Logger.
     *
     * @param bus the bus
     */
    @Inject
    public EventLogger(@Named("eventBus") final Bus bus) {
        bus.register(this);
    }

    /**
     * On Lifecycle Event.
     *
     * @param event the Lifecycle event
     */
    @Subscribe
    public void on(final LifecycleEvent event) {
        log.info(event.getMessage());
    }

}
