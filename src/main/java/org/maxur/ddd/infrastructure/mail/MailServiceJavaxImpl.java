package org.maxur.ddd.infrastructure.mail;

import org.maxur.ddd.domain.Mail;
import org.maxur.ddd.account.service.MailService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class MailServiceJavaxImpl implements MailService {

    private static final int DEFAULT_SMTP_PORT = 25;

    private static final String DEFAULT_SMTP_HOST = "127.0.0.1";

    private final String fromAddress;

    private final Properties props;


    public MailServiceJavaxImpl(final String fromAddress) {
        this(fromAddress, DEFAULT_SMTP_HOST, DEFAULT_SMTP_PORT);
    }

    private MailServiceJavaxImpl(final String fromAddress, final String host, final int port) {
        this.fromAddress = fromAddress;
        props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
    }

    @Override
    public void send(final Mail mail) throws MessagingException {
        Transport.send(makeMessageBy(mail));
    }

    private Message makeMessageBy(final Mail mail) throws MessagingException {
        final Message message = prepareMessage(mail, getSession());
        final Multipart multipart = new MimeMultipart();
        addTextPart(mail, multipart);
        message.setContent(multipart);
        return message;
    }

    private void addTextPart(final Mail mail, final Multipart multipart) throws MessagingException {
        final BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(mail.getBody());
        multipart.addBodyPart(messageBodyPart);
    }

    private Message prepareMessage(final Mail mail, final Session session) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getToAddress()));
        message.setSubject(mail.getSubject());
        return message;
    }

    private Session getSession() {
        return Session.getInstance(props);
    }

}