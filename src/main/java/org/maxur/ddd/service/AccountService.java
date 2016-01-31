package org.maxur.ddd.service;

import org.maxur.ddd.domain.*;

import javax.inject.Inject;
import java.util.List;

import static org.maxur.ddd.domain.ServiceLocatorProvider.service;
import static org.maxur.ddd.domain.User.newUser;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class AccountService {

    private final UserDao userDao;

    private final TeamDao teamDao;

    @Inject
    public AccountService(UserDao userDao, TeamDao teamDao) {
        this.userDao = userDao;
        this.teamDao = teamDao;
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
        return result;
    }

    public User changeUserInfo(Id<User> id, Person person, Id<Team> teamId) throws BusinessException {
        UnitOfWork uof = uof();
        User user = getUser(id);
        Team team = getTeam(teamId);
        Team oldTeam = getTeam(user.getTeamId());
        user.changeInfo(person, team);
        uof.modify(user);
        uof.modify(oldTeam);
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
    }

    public void deleteUserBy(Id<User> id) throws BusinessException {
        UnitOfWork uof = uof();
        User user = getUser(id);
        Team team = getTeam(user.getTeamId());
        user.fire();
        uof.remove(user);
        uof.modify(team);
        uof.commit();
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
