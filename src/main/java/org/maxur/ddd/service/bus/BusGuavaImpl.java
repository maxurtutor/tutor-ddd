package org.maxur.ddd.service.bus;

import com.google.common.eventbus.EventBus;
import lombok.experimental.Delegate;
import org.maxur.ddd.domain.Event;

/**
 * The type Bus guava.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public final class BusGuavaImpl implements Bus {

    @Delegate
    private final EventBus eventBus;

    private BusGuavaImpl(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Bus bus guava.
     *
     * @return the bus guava
     */
    public static BusGuavaImpl bus() {
        return new BusGuavaImpl(new EventBus(new EventBusLoggingHandler()));
    }

    @Override
    public void post(final Event event) {
        eventBus.post(event);
    }

    @Override
    public String id() {
        return identifier();
    }
}
