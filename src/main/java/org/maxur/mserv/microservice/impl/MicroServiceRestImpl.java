/*
 * Copyright 2015 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 */

package org.maxur.mserv.microservice.impl;

import eu.infomas.annotation.AnnotationDetector;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;
import org.maxur.mserv.bus.Bus;
import org.maxur.mserv.ioc.ServiceLocator;
import org.maxur.mserv.microservice.Configuration;
import org.maxur.mserv.microservice.MicroService;
import org.maxur.mserv.microservice.Observer;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static org.maxur.mserv.microservice.ServiceStartedEvent.serviceStartedEvent;
import static org.maxur.mserv.microservice.ServiceStoppedEvent.serviceStoppedEvent;

/**
 * Rest Micro Service.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>9/15/2015</pre>
 */
@Slf4j
public class MicroServiceRestImpl implements MicroService {

    //  private final WebServer webServer;

    //  private final ConfigParamsLogger configParamsLogger;

    private final Bus bus;

    private final ServiceLocator locator;

    private volatile boolean keepRunning = true;

    private String[] packageNames = {};

    /**
     * Instantiates a new Micro service rest.
     * <p>
     * //  * @param webServer          webServer
     *
     * @param bus     the Event bus
     * @param locator the Service Locator
     */
    @Inject
    public MicroServiceRestImpl(
        // final WebServer webServer,
        final Bus bus,
        final ServiceLocator locator
    ) {
        //    this.webServer = webServer;
        //    this.configParamsLogger = configParamsLogger;
        this.bus = bus;
        this.locator = locator;

        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                keepRunning = false;
                try {
                    mainThread.join();
                    bus.post(serviceStoppedEvent());
                } catch (InterruptedException e) {
                    log.error("Error on stop service: ", e);
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }
        });

    }

    public boolean isKeepRunning() {
        return keepRunning;
    }


    @Override
    public final void start() {
        scanPackage();
//        webServer.start();
        bus.post(serviceStartedEvent());
//        configParamsLogger.validateParams();
    }

    @Override
    public final void stop() {
//        webServer.stop();
        System.exit(0);
    }

    @Override
    public MicroService inPackages(final String... packageNames) {
        this.packageNames = packageNames;
        return this;
    }

    private void scanPackage() {

        final AnnotationDetector.TypeReporter reporter = new AnnotationDetector.TypeReporter() {

            @Override
            public void reportTypeAnnotation(final Class<? extends Annotation> aClass, final String className) {
                if (aClass.equals(Observer.class)) {
                    makeObserverBy(className);
                }
                if (aClass.equals(Configuration.class)) {
                    makeConfigurationBy(className);
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public Class<? extends Annotation>[] annotations() {
                return new Class[]{Observer.class, Configuration.class};
            }

        };

        final AnnotationDetector cf = new AnnotationDetector(reporter);
        try {
            if (packageNames.length == 0) {
                cf.detect();
            } else {
                cf.detect(packageNames);
            }
        } catch (IOException e) {
            log.error("Error on detect annotated classes: ", e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void makeConfigurationBy(final String className) {
        final Class<?> clazz = getClassByName(className);
        final Configuration clazzAnnotation = clazz.getAnnotation(Configuration.class);
        final String fileName = clazzAnnotation.fileName();
        if (!isNullOrEmpty(fileName)) {
           loadConfig();
        }
    }

    private void loadConfig() {
        //TODO Implement It
    }

    private void makeObserverBy(final String className) {
        final Class<?> clazz = getClassByName(className);
        final Object observer;
        if (clazz.isAnnotationPresent(Service.class)) {
            observer = locator.bean(clazz);
        } else {
            observer = createClassInstance(clazz);
        }

        bus.register(observer);
    }

    private Object createClassInstance(final Class<?> clazz) {
        try {
            final Constructor<?> ctor = clazz.getConstructor();
            return ctor.newInstance();
        } catch (InstantiationException | InvocationTargetException e) {
            log.error("Error on create observer: ", e);
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error(format("Error on create observer (Illegal Access to Class %s): ", clazz.getSimpleName()), e);
            throw new IllegalStateException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(format("Error on create observer (Constructor of Class %s not found) : ",
                clazz.getSimpleName()), e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private Class<?> getClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error(format("Error on create observer (Class %s not found): ", className), e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
