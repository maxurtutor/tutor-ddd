package org.maxur.ddd.service;

import org.maxur.ddd.dao.AccountDao;
import org.maxur.ddd.dao.GroupDAO;
import org.maxur.ddd.dao.UserDAO;
import org.maxur.ddd.domain.Group;
import org.maxur.ddd.domain.User;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class AccountService {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    final private Logger logger = LoggerFactory.getLogger(AccountService.class);

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    private final DBI dbi;

    private final MailService mailService;

    @Inject
    public AccountService(DBI dbi, MailService mailService) {
        this.dbi = dbi;
        this.mailService = mailService;
    }

    public User create(User user) throws ValidationException {
        validate(user);
        final String groupId = user.getGroupId();
        Group group = dbi.onDemand(GroupDAO.class).findById(groupId);
        if (group == null) {
            throw new NotFoundException("Group", groupId);
        }
        Integer countByGroup = dbi.onDemand(UserDAO.class).findCountByGroup(groupId);
        if (Objects.equals(countByGroup, group.getMaxCapacity())) {
            throw new ValidationException("More users than allowed in group");
        }
        dbi.onDemand(AccountDao.class).save(user, group);

        Mail mail = new Mail("DDD System Notification", "You has be registered by DDD System", user.getEmail());
        try {
            mailService.send(mail);
        } catch (MessagingException e) {
            logger.error("Unable to send email", e);
        }
        return user;
    }

    private void validate(User user) throws ValidationException {
        if (user.getId() == null || user.getId().isEmpty()) {
            throw new ValidationException("User Id must not be empty");
        }
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            throw new ValidationException("User First Name must not be empty");
        }
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            throw new ValidationException("User Last Name must not be empty");
        }
        Matcher matcher = pattern.matcher(user.getEmail());
        if (!matcher.matches()) {
            throw new ValidationException("User Email is invalid");
        }
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
