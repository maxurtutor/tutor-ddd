package org.maxur.ddd.service.ioc;

import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.maxur.ddd.Binder;

/**
 * The type Service locator factory hk 2.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public class ServiceLocatorFactoryHk2Impl {

    private final org.glassfish.hk2.api.ServiceLocatorFactory locatorFactory;

    /**
     * Instantiates a new Service locator factory hk 2.
     */
    private ServiceLocatorFactoryHk2Impl() {
        locatorFactory = org.glassfish.hk2.api.ServiceLocatorFactory.getInstance();
    }

    /**
     * Locator service locator.
     *
     * @param name   the name
     * @param binder the binder
     * @return the service locator
     */
    public static ServiceLocator locator(final String name, final Binder binder) {
        return new ServiceLocatorFactoryHk2Impl().newLocator(name, binder);
    }


    private ServiceLocator newLocator(final String name, final Binder binder) {
        final org.glassfish.hk2.api.ServiceLocator serviceLocator = locatorFactory.create(name);
        ServiceLocatorUtilities.bind(serviceLocator, binder);
        final ServiceLocatorHk2Impl result = new ServiceLocatorHk2Impl(serviceLocator);
        binder.bind(result).to(ServiceLocator.class);
        return result;
    }

}
