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

import java.net.URI;

/**
 * The interface Properties service.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>30.08.2015</pre>
 */
public interface PropertiesService {

    /**
     * return properties by key
     *
     * @param key properties key
     * @return properties by key
     */
    String asString(String key);

    /**
     * return properties by key
     *
     * @param key properties key
     * @return properties by key
     */
    Long asLong(String key);

    /**
     * return properties by key
     *
     * @param key properties key
     * @return properties by key
     */
    Integer asInteger(String key);

    /**
     * return properties by key
     *
     * @param key properties key
     * @return properties by key
     */
    URI asURI(String key);
}
