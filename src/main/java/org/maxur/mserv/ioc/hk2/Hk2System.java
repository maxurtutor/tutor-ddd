package org.maxur.mserv.ioc.hk2;

import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.jvnet.hk2.annotations.Service;
import org.maxur.ddd.config.ConfigResolver;
import org.maxur.mserv.aop.ConfigurationInjectionResolver;
import org.maxur.mserv.aop.HK2InterceptionService;
import org.maxur.mserv.bus.Bus;
import org.maxur.mserv.bus.BusGuavaImpl;
import org.maxur.mserv.core.annotation.Param;
import org.maxur.mserv.ioc.ServiceLocator;
import org.maxur.mserv.microservice.MicroService;
import org.maxur.mserv.microservice.impl.MicroServiceRestImpl;
import org.maxur.mserv.web.WebServer;
import org.maxur.mserv.web.grizzly.WebServerGrizzlyImpl;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

import static org.maxur.mserv.reflection.ClassUtils.createClassInstance;

public class Hk2System {

    private ServiceLocator locator;

    public void init(final List<Class<?>> binders) {
        final List<AbstractBinder> list = binders(binders);
        locator = ServiceLocatorFactoryHk2Impl.locator(list.toArray(new AbstractBinder[list.size()]));
    }

    public ConfigResolver configResolver() {
        return (ConfigResolver)
            locator.bean(InjectionResolver.class, "config.resolver");
    }

    public Object instanceOf(final Class<?> clazz) {
        return clazz.isAnnotationPresent(Service.class) ?
            locator.bean(clazz) :
            createClassInstance(clazz);
    }

    public ServiceLocator locator() {
        return locator;
    }

    private List<AbstractBinder> binders(List<Class<?>> binders) {
        final List<AbstractBinder> result = binders.stream()
            .map(this::instanceOf)
            .filter(AbstractBinder.class::isInstance)
            .map(AbstractBinder.class::cast)
            .collect(Collectors.toList());
        result.add(new Hk2Binder());
        return result;
    }

    private static class Hk2Binder extends AbstractBinder {

        private static final TypeLiteral<InjectionResolver<Param>> INJECTION_RESOLVER_TYPE_LITERAL =
            new TypeLiteral<InjectionResolver<Param>>() {
            };

        @Override
        protected void configure() {

            this.bind(ConfigurationInjectionResolver.class)
                .to(INJECTION_RESOLVER_TYPE_LITERAL)
                .named("config.resolver")
                .in(Singleton.class);

            this.bind(ServiceLocatorHk2Impl.class).to(ServiceLocator.class).in(Singleton.class);
            this.bind(HK2InterceptionService.class).to(InterceptionService.class).in(Singleton.class);
            this.bind(BusGuavaImpl.class).to(Bus.class).named("event.bus").in(Singleton.class);
            this.bind(BusGuavaImpl.class).to(Bus.class).named("command.bus").in(Singleton.class);
            this.bind(WebServerGrizzlyImpl.class).to(WebServer.class).in(Singleton.class);
            this.bind(MicroServiceRestImpl.class)
                .to(MicroService.class)
                .named("rest.service")
                .in(Singleton.class);
        }

    }


}

