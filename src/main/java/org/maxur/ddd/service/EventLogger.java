package org.maxur.ddd.service;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.maxur.ddd.service.bus.Bus;

/**
 * The type Logger.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
@Slf4j
public class EventLogger {

    /**
     * Instantiates a new Logger.
     *
     * @param bus the bus
     */
    public EventLogger(final Bus bus) {
        bus.register(this);
    }

    @Subscribe
    public void on(final ServiceInitializedEvent event) {
        log.info("Service is initialized");

    }

    @Subscribe
    public void on(final ServiceStartedEvent event) {
        log.info("Service is started");
    }

    @Subscribe
    public void on(final ServiceStoppedEvent event) {
        log.info("Service is stopped");
    }

}
