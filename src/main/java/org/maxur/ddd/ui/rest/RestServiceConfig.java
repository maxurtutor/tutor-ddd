/*
 * Copyright 2015 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 */


package org.maxur.ddd.ui.rest;

import org.glassfish.jersey.ServiceLocatorProvider;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Rest Service Configuration
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>30.08.2015</pre>
 */
public class RestServiceConfig extends ResourceConfig {

    /**
     * constructor
     */
    public RestServiceConfig() {
        setApplicationName("notification-adapter");
        packages("ru.russianpost.hms.notificationadapter.rest");
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);

        register(JacksonFeature.class);
        register(RuntimeExceptionHandler.class);
        register(ValidationExceptionHandler.class);
        register(new ServiceLocatorFeature());
        register(NotificationApplicationEventListener.class);
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
