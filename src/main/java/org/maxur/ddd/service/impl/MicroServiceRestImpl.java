/*
 * Copyright 2015 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 */

package org.maxur.ddd.service.impl;

import org.maxur.ddd.service.MicroService;
import org.maxur.ddd.service.bus.Bus;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.maxur.ddd.service.ServiceInitializedEvent.serviceInitializedEvent;
import static org.maxur.ddd.service.ServiceStartedEvent.serviceStartedEvent;
import static org.maxur.ddd.service.ServiceStoppedEvent.serviceStoppedEvent;

/**
 * Rest Micro Service.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>9/15/2015</pre>
 */
public class MicroServiceRestImpl implements MicroService {

  //  private final WebServer webServer;

  //  private final ConfigParamsLogger configParamsLogger;

    private final Bus bus;

    private volatile boolean keepRunning = true;

    /**
     * Instantiates a new Micro service rest.
     *
   //  * @param webServer          webServer
   //  * @param configParamsLogger configParamsLogger
     * @param bus                the bus
     */
    @Inject
    public MicroServiceRestImpl(
       // final WebServer webServer,
       // final ConfigParamsLogger configParamsLogger,
        final Bus bus
    ) {
    //    this.webServer = webServer;
    //    this.configParamsLogger = configParamsLogger;
        this.bus = bus;

        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                keepRunning = false;
                try {
                    mainThread.join();
                    bus.post(serviceStoppedEvent());
                } catch (InterruptedException e) {
                    new IllegalStateException(e.getMessage(), e);
                }
            }
        });

    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    /**
     * init on post construct
     */
    @PostConstruct
    public final void init() {
        bus.post(serviceInitializedEvent());
    }


    @Override
    public final void start() {
//        webServer.start();
        bus.post(serviceStartedEvent());
//        configParamsLogger.validateParams();
    }

    @Override
    public final void stop() {
//        webServer.stop();
        System.exit(0);
    }


}
