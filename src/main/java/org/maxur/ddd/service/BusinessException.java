package org.maxur.ddd.service;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>9/18/2015</pre>
 */
public class BusinessException extends Exception {

    private static final long serialVersionUID = -7189064682665335451L;

    BusinessException(final String message) {
        super(message);
    }
}
