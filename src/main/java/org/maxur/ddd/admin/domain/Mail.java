package org.maxur.ddd.admin.domain;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>13.12.2015</pre>
 */
public class Mail {

    private final String subject;
    private final String body;
    private final String responseAddress;

    public Mail(String subject, String body, String responseAddress) {
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

    public String getToAddress() {
        return responseAddress;
    }

}