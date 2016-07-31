package org.maxur.mserv;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.jvnet.hk2.annotations.Service;
import org.maxur.mserv.core.annotation.Binder;
import org.maxur.mserv.core.annotation.Configuration;
import org.maxur.mserv.core.annotation.Observer;
import org.maxur.mserv.core.annotation.Param;
import org.maxur.mserv.aop.ConfigurationInjectionResolver;
import org.maxur.mserv.aop.HK2InterceptionService;
import org.maxur.mserv.bus.Bus;
import org.maxur.mserv.bus.BusGuavaImpl;
import org.maxur.mserv.config.ConfigFile;
import org.maxur.mserv.ioc.ServiceLocator;
import org.maxur.mserv.ioc.ServiceLocatorHk2Impl;
import org.maxur.mserv.microservice.MicroService;
import org.maxur.mserv.microservice.impl.MicroServiceRestImpl;
import org.maxur.mserv.reflection.ClassRepository;
import org.maxur.mserv.web.WebServer;
import org.maxur.mserv.web.impl.WebServerGrizzlyImpl;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.maxur.mserv.ioc.ServiceLocatorFactoryHk2Impl.locator;
import static org.maxur.mserv.reflection.ClassRepository.byPackages;
import static org.maxur.mserv.reflection.ClassUtils.createClassInstance;

/**
 * The type Maxur system.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>01.08.2016</pre>
 */
@Slf4j
public class MaxurSystem {

    private final String name;

    private final List<Class<?>> binders = new ArrayList<>();

    private final List<Class<?>> observers = new ArrayList<>();

    private final List<Class<?>> configurations = new ArrayList<>();

    private ServiceLocator locator;

    private MaxurSystem(final String name) {
        this.name = name;
    }

    /**
     * System maxur system.
     *
     * @param name the name
     * @return the maxur system
     */
    public static MaxurSystem system(final String name) {
        return new MaxurSystem(name);
    }

    /**
     * With aop in packages maxur system.
     *
     * @param packageNames the package names
     * @return the maxur system
     */
    public MaxurSystem withAopInPackages(final String... packageNames) {
        final ClassRepository classRepository = byPackages(packageNames);
        classRepository.addRule(Observer.class, this::collectObserverBy);
        classRepository.addRule(Binder.class, this::collectBinderBy);
        classRepository.addRule(Configuration.class, this::collectConfigurationBy);
        classRepository.scanPackage();
        return this;
    }

    private void collectBinderBy(final Class<?> aClass) {
        binders.add(aClass);
    }

    private void collectConfigurationBy(final Class<?> aClass) {
        configurations.add(aClass);
    }

    private void collectObserverBy(final Class<?> aClass) {
        observers.add(aClass);
    }

    /**
     * Start.
     *
     * @param serviceName the service name
     */
    public void start(final String serviceName) {
        final List<AbstractBinder> list = binders();
        final AbstractBinder[] array = list.toArray(new AbstractBinder[list.size()]);
        locator = locator(array);
        config();
        addObservers();
        locator.bean(MicroService.class, serviceName)
                .withName(name)
                .start();
    }

    // TODO hardcode
    private List<AbstractBinder> binders() {
        final List<AbstractBinder> result = binders.stream()
                .map(this::instanceOf)
                .filter(AbstractBinder.class::isInstance)
                .map(AbstractBinder.class::cast)
                .collect(toList());
        result.add(new Hk2Binder());
        return result;
    }

    private void addObservers() {
        observers.forEach(this::makeObserverBy);
    }

    private void config() {
        switch (configurations.size()) {
            case 0:
                log.warn("Configuration class (with 'Configuration' annotation) is not found");
                return;
            case 1:
                makeConfigurationBy(configurations.get(0));
                break;
            default:
                log.error("More than one configuration class (with 'Configuration' annotation) is found");
        }
    }

    private void makeConfigurationBy(final Class<?> clazz) {
        final Configuration clazzAnnotation = clazz.getAnnotation(Configuration.class);
        final String fileName = clazzAnnotation.fileName();
        final Object config = isNullOrEmpty(fileName) ?
                instanceOf(clazz) :
                loadConfig(fileName, clazz);
        final ConfigurationInjectionResolver resolver = (ConfigurationInjectionResolver)
                locator.bean(InjectionResolver.class, "config.resolver");
        resolver.setConfig(config);

    }

    private Object loadConfig(final String fileName, final Class<?> clazz) {
        final ConfigFile configFile = ConfigFile.yamlFile(fileName);
        try {
            final Object result = configFile.bindTo(clazz);
            log.debug(format("Config file '%s' is loaded. %n %s", fileName, ConfigFile.asYaml(result)));
            return result;
        } catch (RuntimeException e) {
            log.error(format("Config file '%s' is not loaded", fileName));
            log.debug(format("Config file '%s' is not loaded", fileName), e);
            throw e;
        }
    }

    private void makeObserverBy(final Class<?> clazz) {
        locator.bean(Bus.class, "event.bus").register(instanceOf(clazz));
    }

    private Object instanceOf(final Class<?> clazz) {
        return clazz.isAnnotationPresent(Service.class) ?
                locator.bean(clazz) :
                createClassInstance(clazz);
    }

    private static class Hk2Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(ConfigurationInjectionResolver.class)
                    .to(new TypeLiteral<InjectionResolver<Param>>() {
                    })
                    .named("config.resolver")
                    .in(Singleton.class);

            bind(ServiceLocatorHk2Impl.class).to(ServiceLocator.class).in(Singleton.class);
            bind(HK2InterceptionService.class).to(InterceptionService.class).in(Singleton.class);
            bind(BusGuavaImpl.class).to(Bus.class).named("event.bus").in(Singleton.class);
            bind(BusGuavaImpl.class).to(Bus.class).named("command.bus").in(Singleton.class);
            bind(WebServerGrizzlyImpl.class).to(WebServer.class).in(Singleton.class);
            bind(MicroServiceRestImpl.class)
                    .to(MicroService.class)
                    .named("rest.service")
                    .in(Singleton.class);
        }

    }

}
