package org.maxur.ddd.admin.service;


import org.maxur.ddd.admin.domain.Mail;

import javax.mail.MessagingException;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>13.12.2015</pre>
 */
public interface MailService {

    void send(final Mail mail) throws MessagingException;
}
