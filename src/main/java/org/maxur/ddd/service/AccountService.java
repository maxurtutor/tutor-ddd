package org.maxur.ddd.service;

import org.maxur.ddd.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.List;
import java.util.function.BiConsumer;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static org.maxur.ddd.domain.User.newUser;

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
    public AccountService(
            UserDao userDao,
            TeamDao teamDao,
            AccountDao accountDao,
            MailService mailService
    ) {
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

    public User create(
            String name, String firstName, String lastName, String email, String teamId
    ) throws BusinessException {
        User user = newUser(name, firstName, lastName, email, teamId);
        Team team = getTeam(user.getTeamId());
        User result = user.create(team);
        modify(user, team, accountDao::save);
        sendMessage(format("Welcome to team '%s' !", result.getTeamName()), result);
        return result;
    }

    public Entity update(String id, User user) throws BusinessException {
        Team team = getTeam(user.getTeamId());
        User result = user.update(getUser(user.getId().asString()), team);
        modify(user, team, accountDao::update);
        sendMessage(String.format("Welcome to team '%s' !", result.getTeamName()), result);
        return result;
    }

    public void delete(String id) throws BusinessException {
        User user = getUser(id);
        Team team = getTeam(user.getTeamId());
        modify(user, team, accountDao::delete);
        sendMessage("Good by!", user);
    }

    public void changePassword(String id, String password) throws BusinessException {
        final User user = getUser(id);
        user.changePassword(password);
        try {
            userDao.changePassword(id, user.getPassword());
        } catch (RuntimeException e) {
            throw new BusinessException("Constrains violations");
        }
        sendMessage("You password has been changed", user);
    }

    private void modify(User user, Team team, BiConsumer<User, Team> consumer) throws BusinessException {
        try {
            consumer.accept(user, team);
        } catch (RuntimeException e) {
            logger.error("Unable to modify data: " + e.getMessage());
            throw new BusinessException("Constrains violations");
        }
    }

    private void sendMessage(String message, User user) {
        Mail mail = new Mail("TDDD System Notification", message, user.getEmail());
        try {
            mailService.send(mail);
        } catch (MessagingException e) {
            logger.error("Unable to send email: " + e.getMessage());
        }
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

    private Team getTeam(String teamId) throws NotFoundException {
        Team team = teamDao.findById(teamId);
        if (team == null) {
            throw new NotFoundException("Team", teamId);
        }
        return team;
    }


}
