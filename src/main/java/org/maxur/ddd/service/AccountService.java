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
        user.validate();

        Group group = dbi.onDemand(GroupDAO.class).findById(user.getGroupId());
        if (group == null) {
            throw new NotFoundException("Group", user.getGroupId());
        }

        group.add(user, dbi);

        Mail mail = new Mail("DDD System Notification", "You has be registered by DDD System", user.getEmail());

        try {
            mailService.send(mail);
        } catch (MessagingException e) {
            logger.error("Unable to send email", e);
        }
        return user;
    }

    public User findById(String id) throws NotFoundException {
        final User user = dbi.onDemand(UserDAO.class).findById(id);
        if (user == null) {
            throw new NotFoundException("User", id);
        }
        return user;
    }

    public List<User> findAll() {
        return dbi.onDemand(UserDAO.class).findAll();
    }

}
