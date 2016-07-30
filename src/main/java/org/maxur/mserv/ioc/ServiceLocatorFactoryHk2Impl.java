package org.maxur.mserv.ioc;

import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.maxur.mserv.annotation.Param;
import org.maxur.mserv.aop.ConfigurationInjectionResolver;
import org.maxur.mserv.aop.HK2InterceptionService;
import org.maxur.mserv.bus.Bus;
import org.maxur.mserv.bus.BusGuavaImpl;
import org.maxur.mserv.microservice.MicroService;
import org.maxur.mserv.microservice.impl.MicroServiceRestImpl;
import org.maxur.mserv.web.WebServer;
import org.maxur.mserv.web.impl.WebServerGrizzlyImpl;

import javax.inject.Singleton;

/**
 * The type Service locator factory hk 2.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public final class ServiceLocatorFactoryHk2Impl {

    private static final String LOCATOR_DEFAULT_NAME = "default";

    private ServiceLocatorFactoryHk2Impl() {
    }

    /**
     * Locator service locator.
     *
     * @return the service locator
     */
    public static ServiceLocator locator() {
        return locator(LOCATOR_DEFAULT_NAME);
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
        ServiceLocatorUtilities.bind(serviceLocator, new Hk2Binder());
        return serviceLocator.getService(ServiceLocator.class);
    }

    private static class Hk2Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(ConfigurationInjectionResolver.class)
                    .to(new TypeLiteral<InjectionResolver<Param>>() {
                    })
                    .named("configResolver")
                    .in(Singleton.class);

            bind(ServiceLocatorHk2Impl.class).to(ServiceLocator.class);
            bind(HK2InterceptionService.class).to(InterceptionService.class).in(Singleton.class);
            bind(BusGuavaImpl.class).to(Bus.class).named("eventBus").in(Singleton.class);
            bind(BusGuavaImpl.class).to(Bus.class).named("commandBus").in(Singleton.class);
            bind(WebServerGrizzlyImpl.class).to(WebServer.class).in(Singleton.class);
            bind(MicroServiceRestImpl.class).to(MicroService.class).in(Singleton.class);
        }

    }
}
