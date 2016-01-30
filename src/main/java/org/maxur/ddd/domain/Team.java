package org.maxur.ddd.domain;

import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.maxur.ddd.domain.ServiceLocatorProvider.service;

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
        team.setId(snapshot.getId());
        team.setName(snapshot.getName());
        team.setMaxCapacity(snapshot.getMaxCapacity());
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

    void checkTeamCapacity() throws BusinessException {
        Integer count = service(UserDao.class).findCountByTeam(id);
        if (Objects.equals(count, maxCapacity)) {
            throw new BusinessException("The limit users in team is exceeded");
        }
    }

    public Team.Snapshot getSnapshot() {
        final Snapshot snapshot = new Snapshot();
        snapshot.setId(this.id);
        snapshot.setName(this.name);
        snapshot.setMaxCapacity(this.maxCapacity);
        return snapshot;
    }

    public static class Snapshot {
        private String id;
        private String name;
        private Integer maxCapacity;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getMaxCapacity() {
            return maxCapacity;
        }

        public void setMaxCapacity(Integer maxCapacity) {
            this.maxCapacity = maxCapacity;
        }
    }
}
