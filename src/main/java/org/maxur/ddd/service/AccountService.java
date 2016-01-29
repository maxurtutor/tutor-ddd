package org.maxur.ddd.service;

import org.maxur.ddd.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class AccountService {

    private Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final UserDao userDao;

    private final TeamDao teamDao;

    private final AccountDao accountDao;

    private final MailService mailService;

    @Inject
    public AccountService(UserDao userDao, TeamDao teamDao, AccountDao accountDao, MailService mailService) {
        this.userDao = userDao;
        this.teamDao = teamDao;
        this.accountDao = accountDao;
        this.mailService = mailService;
    }

    public User findById(String id) throws BusinessException {
        return getUser(id);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User create(User user) throws BusinessException {
        final User result = user.create(userDao, teamDao, accountDao);
        sendMessage(format("Welcome to team '%s' !", result.getTeamName()), result);
        return result;
    }

    public void delete(String id) throws BusinessException {
        final User user = getUser(id);
        user.delete(teamDao, accountDao);
        sendMessage("Good by!", user);
    }

    public User update(User user) throws BusinessException {
        final User result = user.update(getUser(user.getId()), userDao, teamDao, accountDao);
        sendMessage(String.format("Welcome to team '%s' !", result.getTeamName()), result);
        return result;
    }

    public void changePassword(String id, String password) throws BusinessException {
        final User user = getUser(id);
        user.changePassword(password, userDao, mailService);
        sendMessage("You password has been changed", user);
    }

    private User getUser(String id) throws BusinessException {
        if (isNullOrEmpty(id)) {
            throw new BusinessException("User Id must not be empty");
        }
        final User user = userDao.findById(id);
        if (user == null) {
            throw new NotFoundException("User", id);
        }
        return user;
    }

    private void sendMessage(String message, User user) throws NotificationException {
        Mail mail = new Mail("TDDD System Notification", message, user.getEmail());
        try {
            mailService.send(mail);
        } catch (MessagingException e) {
            logger.error("Unable to send email: " + e.getMessage());
        }
    }


}
