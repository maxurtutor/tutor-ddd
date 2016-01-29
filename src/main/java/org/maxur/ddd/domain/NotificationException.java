package org.maxur.ddd.domain;

import javax.mail.MessagingException;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>30.01.2016</pre>
 */
public class NotificationException extends BusinessException {

    public NotificationException(String message, MessagingException cause) {
        super(message + cause.getMessage());
    }

}
