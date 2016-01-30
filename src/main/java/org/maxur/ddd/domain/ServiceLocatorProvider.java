package org.maxur.ddd.domain;

import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>11/6/2015</pre>
 */
public class ServiceLocatorProvider {

    private static ServiceLocatorProvider instance;

    private final ServiceLocator locator;

    @Inject
    public ServiceLocatorProvider(final ServiceLocator locator) {
        instance = this;
        this.locator = locator;
    }

    public static <T> T service(Class<T> clazz) {
        return instance.locator.getService(clazz);
    }

}
