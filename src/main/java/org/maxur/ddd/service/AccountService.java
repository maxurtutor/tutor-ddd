package org.maxur.ddd.service;

import org.maxur.ddd.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.List;

import static java.lang.String.format;
import static org.maxur.ddd.domain.ServiceLocatorProvider.service;
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

    private final MailService mailService;

    @Inject
    public AccountService(UserDao userDao, TeamDao teamDao, MailService mailService) {
        this.userDao = userDao;
        this.teamDao = teamDao;
        this.mailService = mailService;
    }

    public User findUserById(Id<User> userId) throws BusinessException {
        return getUser(userId);
    }

    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    public User createUserBy(String name, Person person, Id<Team> teamId) throws BusinessException {
        UnitOfWork uof = uof();
        User user = newUser(name, teamId, person);
        Team team = getTeam(teamId);
        User result = user.moveTo(team);
        uof.create(user);
        uof.modify(team);
        uof.commit();
        sendMessage(format("Welcome to team '%s' !", result.getTeamName()), result);
        return result;
    }

    public User changeUserInfo(Id<User> id, Person person, Id<Team> teamId) throws BusinessException {
        UnitOfWork uof = uof();
        User user = getUser(id);
        Team team = getTeam(teamId);
        Team oldTeam = getTeam(user.getTeamId());
        user.changeInfo(person, team);
        // TODO
        if (!oldTeam.equals(team)) {
            sendMessage(String.format("Welcome to team '%s' !", team.getName()), user);
            uof.modify(oldTeam);
        }
        uof.modify(user);
        uof.modify(team);
        uof.commit();
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
        UnitOfWork uof = uof();
        User user = getUser(id);
        Team team = getTeam(user.getTeamId());
        uof.remove(user);
        uof.modify(team);
        uof.commit();
        sendMessage("Good by!", user);
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

    private UnitOfWork uof() {
        return service(UnitOfWork.class);
    }


}
