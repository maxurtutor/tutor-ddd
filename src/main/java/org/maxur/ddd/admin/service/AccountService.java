package org.maxur.ddd.admin.service;

import org.maxur.ddd.commons.domain.Person;
import org.maxur.ddd.admin.domain.User;
import org.maxur.ddd.admin.domain.UserRepository;
import org.maxur.ddd.commons.domain.BusinessException;
import org.maxur.ddd.commons.domain.Id;
import org.maxur.ddd.commons.domain.NotFoundException;
import org.maxur.ddd.commons.service.IdentificationMap;
import org.maxur.ddd.commons.service.UnitOfWork;
import org.maxur.ddd.planning.domain.Team;
import org.maxur.ddd.planning.domain.TeamRepository;

import javax.inject.Inject;
import java.util.List;

import static org.maxur.ddd.admin.domain.User.newUser;
import static org.maxur.ddd.commons.domain.ServiceLocatorProvider.service;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class AccountService {

    private final UserRepository userRepository;

    private final TeamRepository teamRepository;

    @Inject
    public AccountService(UserRepository userRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    public User findUserById(Id<User> userId) throws BusinessException {
        return getUser(userId);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User createUserBy(String name, Person person, Id<Team> teamId) throws BusinessException {
        UnitOfWork uof = uof();
        IdentificationMap im = new IdentificationMap();
        User user = newUser(name, person);
        Team team = getTeam(im, teamId);
        User result = user.moveTo(team);
        uof.create(user);
        uof.modify(team);
        uof.commit();
        im.clear();
        return result;
    }

    public User changeUserInfo(Id<User> id, Person person, Id<Team> teamId) throws BusinessException {
        UnitOfWork uof = uof();
        IdentificationMap im = new IdentificationMap();
        User user = getUser(im, id);
        Team team = getTeam(im, teamId);
        Team oldTeam = user.getTeam();
        user.changeInfo(person, team);
        uof.modify(user);
        uof.modify(oldTeam);
        uof.modify(team);
        uof.commit();
        im.clear();
        return user;
    }

    public void changeUserPassword(Id<User> id, String password) throws BusinessException {
        UnitOfWork uof = uof();
        IdentificationMap im = new IdentificationMap();
        User user = getUser(im, id);
        user.changePassword(password);
        uof.modify(user);
        uof.commit();
        im.clear();
    }

    public void deleteUserBy(Id<User> id) throws BusinessException {
        UnitOfWork uof = uof();
        IdentificationMap im = new IdentificationMap();
        User user = getUser(im, id);
        user.fire();
        uof.remove(user);
        uof.modify(user.getTeam());
        uof.commit();
        im.clear();
    }

    private User getUser(IdentificationMap im, Id<User> id) throws NotFoundException {
        User user = im.get(id);
        if (user == null) {
            user = getUser(id);
            im.put(user);
        }
        return user;
    }

    private User getUser(Id<User> id) throws NotFoundException {
        User user;
        user = userRepository.findById(id.asString());
        if (user == null) {
            throw new NotFoundException("User", id.asString());
        }
        return user;
    }

    private Team getTeam(IdentificationMap im, Id<Team> id) throws NotFoundException {
        Team team = im.get(id);
        if (team == null) {
            team = getTeam(id);
            im.put(team);
        }
        return team;
    }

    private Team getTeam(Id<Team> id) throws NotFoundException {
        Team team;
        team = teamRepository.findById(id.asString());
        if (team == null) {
            throw new NotFoundException("Team", id.asString());
        }
        return team;
    }

    private UnitOfWork uof() {
        return service(UnitOfWork.class);
    }


}
