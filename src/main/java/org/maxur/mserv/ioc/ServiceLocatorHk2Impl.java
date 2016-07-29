package org.maxur.mserv.ioc;

import java.lang.annotation.Annotation;

/**
 * The type Service locator hk 2.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
class ServiceLocatorHk2Impl implements ServiceLocator {

    private final org.glassfish.hk2.api.ServiceLocator serviceLocator;

    /**
     * Instantiates a new Service locator hk 2.
     *
     * @param serviceLocator the service locator
     */
    ServiceLocatorHk2Impl(org.glassfish.hk2.api.ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    @Override
    public <T> T bean(final Class<T> aClass, final Annotation... annotations) {
        return serviceLocator.getService(aClass, annotations);
    }
}
