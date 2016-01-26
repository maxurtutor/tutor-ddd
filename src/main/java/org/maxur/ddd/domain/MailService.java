package org.maxur.ddd.domain;

import org.maxur.ddd.domain.Mail;

import javax.mail.MessagingException;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>13.12.2015</pre>
 */
public interface MailService {

    void send(final Mail mail) throws MessagingException;
}
