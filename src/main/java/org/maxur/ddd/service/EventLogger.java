package org.maxur.ddd.service;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;
import org.maxur.ddd.service.bus.Bus;

import javax.inject.Inject;

/**
 * The type Logger.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
@Slf4j
@Service
public class EventLogger {

    /**
     * Instantiates a new Logger.
     *
     * @param bus the bus
     */
    @Inject
    public EventLogger(final Bus bus) {
        bus.register(this);
    }

    /**
     * On.
     *
     * @param event the event
     */
    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void on(final ServiceInitializedEvent event) {
        log.info("Service is initialized");
    }

    /**
     * On.
     *
     * @param event the event
     */
    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void on(final ServiceStartedEvent event) {
        log.info("Service is started");
    }

    /**
     * On.
     *
     * @param event the event
     */
    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void on(final ServiceStoppedEvent event) {
        log.info("Service is stopped");
    }

}
