package org.maxur.ddd.domain;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>31.01.2016</pre>
 */
public abstract class NotificationService {

    public void send(EmailAddress email, Notification notification, String... args) {
        sendMessage(format(notification.template(), args), email.asString());
    }

    protected abstract void sendMessage(String message, String email);

    public enum Notification {
        WELCOME {
            @Override
            public String template() {
                return "Welcome to team '%s' !";
            }
        },
        CHANGE_PASSWORD {
            @Override
            public String template() {
                return "You password has been changed";
            }
        },
        USER_FIRE {
            @Override
            public String template() {
                return "Good By !";
            }
        };

        public abstract String template();

    }
}
