package org.maxur.ddd.service;

import org.maxur.ddd.domain.*;

import javax.inject.Inject;

import static org.maxur.ddd.domain.ServiceLocatorProvider.service;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>31.01.2016</pre>
 */
public class PlanningService {

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    @Inject
    public PlanningService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public Team createTeam(String name, Integer maxCapacity) throws BusinessException {
        final Team team = Team.newTeam(name, maxCapacity);
        teamRepository.insert(team);
        return team;
    }

    public User moveUser(Id<User> id, Id<Team> teamId) throws BusinessException {
        UnitOfWork uof = uof();
        IdentificationMap im = new IdentificationMap();
        User user = getUser(im, id);
        Team team = getTeam(im, teamId);
        Team oldTeam = getTeam(im, user.getTeamId());
        user.moveTo(team);
        uof.modify(user);
        uof.modify(oldTeam);
        uof.modify(team);
        uof.commit();
        im.clear();
        return user;
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
