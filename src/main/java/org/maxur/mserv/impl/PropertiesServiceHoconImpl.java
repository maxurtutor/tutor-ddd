/*
 * Copyright 2015 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 */

package org.maxur.mserv.impl;

import org.maxur.ddd.service.PropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.function.Function;

/**
 * The type Properties service hocon.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>9/2/2015</pre>
 */
public class PropertiesServiceHoconImpl implements PropertiesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesServiceHoconImpl.class);

/*    private Config defaultConfig;

    private Config userConfig;*/

    /**
     * init post construct
     */
    @PostConstruct
    public void init() {
/*        defaultConfig = ConfigFactory.load().getConfig("DEFAULTS");
        userConfig = ConfigFactory.load().getConfig("NOTIFICATION");*/
    }

    @Override
    public URI asURI(final String key) {
        return URI.create(asString(key));
    }

    @Override
    public String asString(final String key) {
        return getValue(key, this::getString);
    }

    @Override
    public Long asLong(final String key) {
        return getValue(key, this::getLong);
    }

    @Override
    public Integer asInteger(final String key) {
        return getValue(key, this::getInt);
    }

    private <T> T getValue(final String key, final Function<String, T> method) {
/*        try {
            final T value = method.apply(key);
            LOGGER.info("Configuration parameter {} = '{}'", key, value);
            return value;
        } catch (ConfigException.Missing e) {
            LOGGER.error("Configuration parameter '{}' is not found.", key);
            throw e;
        }*/
        return null;
    }

    private String getString(final String key) {
/*        try {
            return userConfig.getString(key);
        } catch (ConfigException.Missing e) {
            return defaultConfig.getString(key);
        }*/
        return null;
    }

    private Integer getInt(final String key) {
/*
        try {
            return userConfig.getInt(key);
        } catch (ConfigException.Missing e) {
            return defaultConfig.getInt(key);
        }
*/
        return null;
    }

    private Long getLong(final String key) {
/*        try {
            return userConfig.getLong(key);
        } catch (ConfigException.Missing e) {
            return defaultConfig.getLong(key);
        }*/
        return null;
    }


}
