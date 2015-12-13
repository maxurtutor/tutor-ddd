package org.maxur.ddd.service;

import org.maxur.ddd.dao.GroupDAO;
import org.maxur.ddd.dao.UserDAO;
import org.maxur.ddd.domain.*;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class AccountService {

    final private Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final DBI dbi;

    private final MailService mailService;

    @Inject
    public AccountService(DBI dbi, MailService mailService) {
        this.dbi = dbi;
        this.mailService = mailService;
    }

    public User create(User user) throws ValidationException {
        Group group = dbi.onDemand(GroupDAO.class).findById(user.getGroupId());
        if (group == null) {
            throw new NotFoundException("Group", user.getGroupId());
        }
        final Notification notification = new Notification();
        group.add(user, dbi, notification);
        if (notification.hasErrors()) {
            throw new ValidationException(notification.errorMessage());
        }
        Mail mail = new Mail("DDD System Notification", "You has be registered by DDD System", user.getEmail());
        try {
            mailService.send(mail);
        } catch (MessagingException e) {
            logger.error("Unable to send email", e);
        }
        return user;
    }

    public User findById(String id) throws ValidationException {
        final User.Snapshot snapshot = dbi.onDemand(UserDAO.class).findById(id);
        if (snapshot == null) {
            throw new NotFoundException("User", id);
        }
        final Notification notification = new Notification();
        if (notification.hasErrors()) {
            throw new ValidationException(notification.errorMessage());
        }
        return User.createFrom(snapshot, notification).orElse(null);
    }

    public List<User> findAll() throws ValidationException {
        final Notification notification = new Notification();
        final List<User> users = dbi.onDemand(UserDAO.class)
                .findAll()
                .stream()
                .map(s -> User.createFrom(s, notification).orElse(null))
                .collect(toList());
        if (notification.hasErrors()) {
            throw new ValidationException(notification.errorMessage());
        }
        return users;
    }

}
