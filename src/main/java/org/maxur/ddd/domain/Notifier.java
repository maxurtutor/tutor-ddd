package org.maxur.ddd.domain;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.Optional;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.01.2016</pre>
 */
public class Notifier {

    private Logger logger = LoggerFactory.getLogger(Notifier.class);

    private final MailService mailService;

    @Inject
    public Notifier(MailService mailService, EventBus eventBus) {
        this.mailService = mailService;
        eventBus.register(this);
    }

    @Subscribe
    public void on(OnPasswordChangedEvent event) {
        final User user = event.getUser();
        final String message = "You password has been changed";
        Mail mail = new Mail("TDDD System Notification", message, user.getEmail());
        try {
            mailService.send(mail);
            event.setResponse(Optional.empty());
        } catch (MessagingException e) {
            logger.error("Unable to send email", e);
            event.setResponse(Optional.of(new RuntimeException(e)));
        }
    }
}
