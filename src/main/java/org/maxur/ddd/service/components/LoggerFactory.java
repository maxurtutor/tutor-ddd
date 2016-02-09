package org.maxur.ddd.service.components;

import org.slf4j.Logger;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>06.02.2016</pre>
 */
public class LoggerFactory {

    public Logger loggerFor(Class<?> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }
}
