package org.maxur.ddd.service;

import org.maxur.ddd.domain.BusinessException;
import org.maxur.ddd.domain.Team;
import org.maxur.ddd.domain.TeamRepository;

import javax.inject.Inject;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>31.01.2016</pre>
 */
public class PlanningService {

    private final TeamRepository teamRepository;

    @Inject
    public PlanningService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team createTeam(String name, Integer maxCapacity) throws BusinessException {
        final Team team = Team.newTeam(name, maxCapacity);
        teamRepository.insert(team);
        return team;
    }
}
