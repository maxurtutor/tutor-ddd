package org.maxur.ddd.service.ioc;

import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * The type Service locator factory hk 2.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public final class ServiceLocatorFactoryHk2Impl {

    private ServiceLocatorFactoryHk2Impl() {
    }

    /**
     * Locator service locator.
     *
     * @param name the name
     * @return the service locator
     */
    public static ServiceLocator locator(final String name) {
        return new ServiceLocatorFactoryHk2Impl().newLocator(name);
    }


    private ServiceLocator newLocator(final String name) {
        final org.glassfish.hk2.api.ServiceLocator serviceLocator =
                ServiceLocatorUtilities.createAndPopulateServiceLocator(name);
        final ServiceLocatorHk2Impl result = new ServiceLocatorHk2Impl(serviceLocator);
        ServiceLocatorUtilities.bind(serviceLocator, new Hk2Binder(result));
        return result;
    }

    private static class Hk2Binder extends AbstractBinder {

        private final ServiceLocator serviceLocator;

        private Hk2Binder(final ServiceLocator serviceLocator) {
            this.serviceLocator = serviceLocator;
        }

        @Override
        protected void configure() {
            bind(serviceLocator).to(ServiceLocator.class);
        }

    }
}
