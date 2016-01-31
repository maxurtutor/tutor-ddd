package org.maxur.ddd.service;

import org.maxur.ddd.domain.Mail;
import org.maxur.ddd.domain.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.MessagingException;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>31.01.2016</pre>
 */
public class NotificationServiceImpl extends NotificationService {

    private final MailService mailService;

    private Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Inject
    public NotificationServiceImpl(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    protected void sendMessage(String message, String email) {
        Mail mail = new Mail("TDDD System Notification", message, email);
        try {
            mailService.send(mail);
        } catch (MessagingException e) {
            logger.error("Unable to send email: " + e.getMessage());
        }
    }

}
