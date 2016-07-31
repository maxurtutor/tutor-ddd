/*
 * Copyright 2015 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 */


package org.maxur.mserv.web.impl;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ServerProperties;
import org.maxur.mserv.bus.Bus;
import org.maxur.mserv.web.WebServer;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

/**
 * web server grizzly impl
 */
@Provider
public class WebServerGrizzlyImpl extends WebServer {

    private HttpServer httpServer;


    private final JerseyResourceConfig config;


    private final ServiceLocator locator;


    /**
     * Instantiates a new Web server grizzly.
     *
     * @param config  the config
     * @param locator the locator
     * @param bus     the bus
     */
    @Inject
    public WebServerGrizzlyImpl(
            final JerseyResourceConfig config,
            final ServiceLocator locator,
            @Named("event.bus") final Bus bus
    ) {
        super(bus);
        this.locator = locator;
        this.config = config;
        enrichConfig();
    }

    private void enrichConfig() {
        this.config.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        this.config.property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
        this.config.register(JacksonFeature.class);
        this.config.register(RuntimeExceptionHandler.class);
        this.config.register(ValidationExceptionHandler.class);
        this.config.register(new ServiceLocatorFeature());
        this.config.register(NotificationApplicationEventListener.class);
    }

    @Override
    protected void launch() {
        makLoggerBridge();
        httpServer = GrizzlyHttpServerFactory.createHttpServer(
                webConfig().restUri(),
                config,
                locator
        );
        webConfig().content().entrySet().forEach(
                e -> httpServer.getServerConfiguration().addHttpHandler(
                                new StaticHttpHandler(e.getKey()),
                WEB_APP_URL + e.getValue())
        );

    }

    private void makLoggerBridge() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    @Override
    protected void shutdown() {
        if (httpServer != null) {
            httpServer.shutdownNow();
        }
    }

    /**
     * service locator feature
     */
    private static class ServiceLocatorFeature implements Feature {

        @Override
        public boolean configure(FeatureContext context) {
            ServiceLocatorProvider.getServiceLocator(context);
            return true;
        }
    }

}
