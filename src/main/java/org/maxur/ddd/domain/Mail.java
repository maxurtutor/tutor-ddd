package org.maxur.ddd.domain;


/**
 * @author myunusov
 * @version 1.0
 * @since <pre>13.12.2015</pre>
 */
public class Mail {

    private final String subject;
    private final String body;
    private final EmailAddress responseAddress;

    public Mail(String subject, String body, EmailAddress responseAddress) {
        this.subject = subject;
        this.body = body;
        this.responseAddress = responseAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public EmailAddress getToAddress() {
        return responseAddress;
    }

}