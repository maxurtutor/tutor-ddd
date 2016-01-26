package org.maxur.ddd.domain;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.01.2016</pre>
 */
public class OnPasswordChangedEvent extends AsyncCallBackEvent<RuntimeException> {

    private final User user;

    public OnPasswordChangedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
