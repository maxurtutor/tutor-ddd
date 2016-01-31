package org.maxur.ddd.service;

import org.maxur.ddd.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.List;
import java.util.function.BiConsumer;

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

    public User findUserById(Id<User> userId) throws BusinessException {
        return getUser(userId);
    }

    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    public User createUserBy(String name, Person person, Id<Team> teamId) throws BusinessException {
        User user = newUser(name, teamId, person);
        Team team = getTeam(teamId);
        User result = user.moveTo(team);
        modify(user, team, accountDao::save);
        sendMessage(format("Welcome to team '%s' !", result.getTeamName()), result);
        return result;
    }

    public User changeUserInfo(Id<User> id, Person person, Id<Team> teamId) throws BusinessException {
        User user = getUser(id);
        Team team = getTeam(teamId);
        Id<Team> oldTeamId = user.getTeamId();
        user.changeInfo(person, team);
        // TODO
        if (oldTeamId.equals(team.getId())) {
            sendMessage(String.format("Welcome to team '%s' !", team.getName()), user);
        }
        modify(user, team, accountDao::update);
        return user;
    }

    public void changeUserPassword(Id<User> id1, String password) throws BusinessException {
        final User user = findUserById(id1);
        user.changePassword(password);
        // TODO
        try {
            userDao.changePassword(id1.asString(), user.getPassword());
        } catch (RuntimeException e) {
            throw new BusinessException("Constrains violations");
        }
        sendMessage("You password has been changed", user);
    }

    public void deleteUserBy(Id<User> id) throws BusinessException {
        User user = getUser(id);
        Team team = getTeam(user.getTeamId());
        sendMessage("Good by!", user);
        modify(user, team, accountDao::delete);
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

    private User getUser(Id<User> id) throws NotFoundException {
        final User user = userDao.findById(id.asString());
        if (user == null) {
            throw new NotFoundException("User", id.asString());
        }
        return user;
    }

    private Team getTeam(Id<Team> teamId) throws NotFoundException {
        Team team = teamDao.findById(teamId.asString());
        if (team == null) {
            throw new NotFoundException("Team", teamId.asString());
        }
        return team;
    }


}
