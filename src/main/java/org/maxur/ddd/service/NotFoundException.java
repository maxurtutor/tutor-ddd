package org.maxur.ddd.service;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>13.11.2015</pre>
 */
public class NotFoundException extends ValidationException {

    private static final long serialVersionUID = -343985451164289078L;

    public NotFoundException(String entityType, String id) {
        super(format("%s (id='%s') is not found", entityType, id));
    }
}
