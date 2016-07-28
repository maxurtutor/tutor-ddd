/*
 * Copyright 2015 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 */

package org.maxur.ddd.service;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Type;

/**
 * The type Configuration injection resolver.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>9/2/2015</pre>
 */
public class ConfigurationInjectionResolver implements InjectionResolver<Named> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationInjectionResolver.class);

    private final PropertiesService propertiesService;

    /**
     * Instantiates a new Configuration injection resolver.
     *
     * @param propertiesService propertiesService
     */
    @Inject
    public ConfigurationInjectionResolver(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> root) {
        Named annotation = injectee.getParent().getAnnotation(Named.class);
        final String name = annotation.value();
        final Type type = injectee.getRequiredType();
        switch (type.getTypeName()) {
            case "java.lang.String":
                return propertiesService.asString(name);
            case "java.lang.Integer":
                return propertiesService.asInteger(name);
            case "java.lang.Long":
                return propertiesService.asLong(name);
            case "java.net.URI":
                return propertiesService.asURI(name);
            default:
                LOGGER.error("Unsupported property type {}", type.getTypeName());
                throw new IllegalStateException(String.format("Unsupported property type %s", type.getTypeName()));
        }
    }

    @Override
    public boolean isConstructorParameterIndicator() {
        return false;
    }

    @Override
    public boolean isMethodParameterIndicator() {
        return false;
    }
}
