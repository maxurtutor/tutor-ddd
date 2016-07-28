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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

/**
 * add here all new parameters
 * Created by Igor Chirkov on 02.11.2015.
 */
public class ConfigParamsLogger {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigParamsLogger.class);

    @SuppressWarnings("unused")
    @Named("file_storage.connect.timeout")
    private String connectTimeout;

    @SuppressWarnings("unused")
    @Named("file_storage.read.timeout")
    private String readTimeout;

    @SuppressWarnings("unused")
    @Named("management.url")
    private String managementUrl;

    @SuppressWarnings("unused")
    @Named("webapp.url")
    private String webappUrl;

    @SuppressWarnings("unused")
    @Named("webapp.folderName")
    private String webappFolderName;

    /**
     * can be add validation of parameters
     */
    public void validateParams() {
        LOG.info("All params is ok");
    }

}
