package org.maxur.ddd.service;

import org.maxur.ddd.domain.BusinessException;
import org.maxur.ddd.domain.Team;
import org.maxur.ddd.domain.TeamDao;

import javax.inject.Inject;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>31.01.2016</pre>
 */
public class PlanningService {

    private final TeamDao teamDao;

    @Inject
    public PlanningService(TeamDao teamDao) {
        this.teamDao = teamDao;
    }

    public Team createTeam(String name, Integer maxCapacity) throws BusinessException {
        final Team team = Team.newTeam(name, maxCapacity);
        teamDao.insert(team.getSnapshot());
        return team;
    }
}
