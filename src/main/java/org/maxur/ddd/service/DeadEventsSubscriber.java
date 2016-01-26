package org.maxur.ddd.service;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.01.2016</pre>
 */
public class DeadEventsSubscriber {

    private Logger logger = LoggerFactory.getLogger(DeadEventsSubscriber.class);

    @Subscribe
    public void handleDeadEvent(DeadEvent deadEvent) {
        logger.warn("Dead Event " + deadEvent.getEvent());
    }
}