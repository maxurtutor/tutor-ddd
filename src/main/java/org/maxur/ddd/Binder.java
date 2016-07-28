/*
 * Copyright 2015 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 */

package org.maxur.ddd;

import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.maxur.ddd.service.EventLogger;
import org.maxur.ddd.service.aop.HK2InterceptionService;
import org.maxur.ddd.service.MicroService;
import org.maxur.ddd.service.bus.Bus;
import org.maxur.ddd.service.impl.MicroServiceRestImpl;

import javax.inject.Singleton;

import static org.maxur.ddd.service.bus.BusGuavaImpl.bus;

/**
 * Application Configurations
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>01.09.2015</pre>
 */
public final class Binder extends AbstractBinder {

    @SuppressWarnings("RedundantToBinding")
    @Override
    protected void configure() {

        bind(HK2InterceptionService.class)
            .to(InterceptionService.class)
            .in(Singleton.class);

        final Bus bus = bus();

        bind(bus).to(Bus.class);

        bind(new EventLogger(bus))
            .to(EventLogger.class);

        bind(MicroServiceRestImpl.class)
            .to(MicroService.class)
            .in(Singleton.class);



/*        bind(ConfigurationInjectionResolver.class)
            .to(new TypeLiteral<InjectionResolver<Named>>() {
            })
            .in(Singleton.class);
        bind(RestServiceConfig.class)
            .to(ResourceConfig.class)
            .in(Singleton.class);

        bind(PropertiesServiceHoconImpl.class)
            .to(PropertiesService.class)
            .in(Singleton.class);
        bind(WebServerGrizzlyImpl.class)
            .to(WebServer.class)
            .in(Singleton.class);

        bind(ConfigParamsLogger.class)
            .to(ConfigParamsLogger.class)
            .in(Singleton.class);*/
    }

}
