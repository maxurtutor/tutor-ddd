package org.maxur.ddd.domain;

import org.maxur.ddd.service.UserDao;

import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class Team {

    private String id;

    private String name;

    private Integer maxCapacity;

    private Team() {
    }

    public static Team restore(Snapshot snapshot) throws BusinessException {
        final Team team = new Team();
        team.setId(snapshot.id);
        team.setName(snapshot.name);
        team.setMaxCapacity(snapshot.maxCapacity);
        return team;
    }

    private void setId(String id) throws BusinessException {
        if (isNullOrEmpty(id)) {
            throw new BusinessException("Team Id must not be empty");
        }
        this.id = id;
    }

    private void setName(String name) throws BusinessException {
        if (isNullOrEmpty(name)) {
            throw new BusinessException("Team name must not be empty");
        }
        this.name = name;
    }

    private void setMaxCapacity(Integer maxCapacity) throws BusinessException {
        if (maxCapacity < 1) {
            throw new BusinessException("Team max Capacity must be more than 0");
        }
        this.maxCapacity = maxCapacity;
    }

    void checkTeamCapacity(UserDao userDao) throws BusinessException {
        Integer count = userDao.findCountByTeam(id);
        if (Objects.equals(count, maxCapacity)) {
            throw new BusinessException("The limit users in team is exceeded");
        }
    }

    public Team.Snapshot getSnapshot() {
        final Snapshot snapshot = new Snapshot();
        snapshot.id = this.id;
        snapshot.name = this.name;
        snapshot.maxCapacity = this.maxCapacity;
        return snapshot;
    }

    public static class Snapshot {
        public String id;
        public String name;
        public Integer maxCapacity;
    }
}
