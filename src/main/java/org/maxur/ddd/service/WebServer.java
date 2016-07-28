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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.net.URI;

/**
 * This Abstract class represents interface of Web Server.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>30.08.2015</pre>
 */
public abstract class WebServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

    /**
     * webapp folder url
     */
    protected static final String WEB_APP_URL = "/";


    /**
     * WebApp URL
     */
    @SuppressWarnings("unused")
    @Named("webapp.url")
    private URI webappUrl;

    /**
     * Start Web server.
     */
    public void start() {
        LOGGER.info("Start Web Server");
        launch();
        LOGGER.info("Starting on " + webappUrl);
    }


    /**
     * Stop Web server.
     */
    public void stop() {
        LOGGER.info("Stop Web Server");
        shutdown();
    }


    /**
     * Gets webapp url.
     *
     * @return the webapp url
     */
    public URI getWebappUrl() {
        return webappUrl;
    }

    /**
     * web server launch
     */
    protected abstract void launch();

    /**
     * web server shutdown
     */
    protected abstract void shutdown();


}
