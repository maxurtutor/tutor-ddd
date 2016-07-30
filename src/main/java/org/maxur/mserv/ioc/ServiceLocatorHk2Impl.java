package org.maxur.mserv.ioc;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.hk2.api.InjectionResolver;
import org.jvnet.hk2.annotations.Service;
import org.maxur.mserv.annotation.Configuration;
import org.maxur.mserv.annotation.Observer;
import org.maxur.mserv.aop.ConfigurationInjectionResolver;
import org.maxur.mserv.bus.Bus;
import org.maxur.mserv.config.ConfigFile;
import org.maxur.mserv.reflection.ClassRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static org.maxur.mserv.reflection.ClassRepository.byPackages;
import static org.maxur.mserv.reflection.ClassUtils.createClassInstance;

/**
 * The type Service locator hk 2.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
@Slf4j
class ServiceLocatorHk2Impl implements ServiceLocator {

    private final org.glassfish.hk2.api.ServiceLocator serviceLocator;

    private final Bus bus;

    /**
     * Instantiates a new Service locator hk 2.
     *
     * @param serviceLocator the service locator
     * @param bus            the Event Bus
     */
    @Inject
    public ServiceLocatorHk2Impl(org.glassfish.hk2.api.ServiceLocator serviceLocator, @Named("eventBus") Bus bus) {
        this.serviceLocator = serviceLocator;
        this.bus = bus;
    }

    @Override
    public <T> T bean(final Class<T> aClass, final Annotation... annotations) {
        return serviceLocator.getService(aClass, annotations);
    }

    @Override
    public ServiceLocator withAopInPackages(final String... packageNames) {
        final ClassRepository classRepository = byPackages(packageNames);
        classRepository.addRule(Observer.class, this::makeObserverBy);
        classRepository.addRule(Configuration.class, this::makeConfigurationBy);
        classRepository.scanPackage();
        return this;
    }

    private void makeConfigurationBy(final Class<?> clazz) {
        final Configuration clazzAnnotation = clazz.getAnnotation(Configuration.class);
        final String fileName = clazzAnnotation.fileName();
        final Object config = isNullOrEmpty(fileName) ?
                getInstanceOf(clazz) :
                loadConfig(fileName, clazz);
        final ConfigurationInjectionResolver resolver = (ConfigurationInjectionResolver)
                serviceLocator.getService(InjectionResolver.class, "configResolver");
        resolver.setConfig(config);

    }

    private Object loadConfig(final String fileName, final Class<?> clazz) {
        final ConfigFile configFile = ConfigFile.yamlFile(fileName);
        try {
            return configFile.bindTo(clazz);
        } catch (RuntimeException e) {
            log.error(format("Config file '%s' is not loaded", fileName));
            log.debug(format("Config file '%s' is not loaded", fileName), e);
            throw e;
        }
    }

    private void makeObserverBy(final Class<?> clazz) {
        bus.register(getInstanceOf(clazz));
    }

    private Object getInstanceOf(final Class<?> clazz) {
        return clazz.isAnnotationPresent(Service.class) ?
                bean(clazz) :
                createClassInstance(clazz);
    }

}
