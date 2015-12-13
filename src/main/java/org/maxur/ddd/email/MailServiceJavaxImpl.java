package org.maxur.ddd.email;

import org.maxur.ddd.domain.EmailAddress;
import org.maxur.ddd.domain.Mail;
import org.maxur.ddd.domain.MailService;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
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

    public static final int DEFAULT_SMTP_PORT = 25;

    public static final String DEFAULT_SMTP_HOST = "127.0.0.1";

    private final EmailAddress fromAddress;

    private final Properties props;


    public MailServiceJavaxImpl(final EmailAddress fromAddress) {
        this(fromAddress, DEFAULT_SMTP_HOST, DEFAULT_SMTP_PORT);
    }

    public MailServiceJavaxImpl(final EmailAddress fromAddress, final String host, final int port) {
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
        message.setFrom(new InternetAddress(fromAddress.toString()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getToAddress().asString()));
        message.setSubject(mail.getSubject());
        return message;
    }

    private Session getSession() {
        return Session.getInstance(props);
    }

}