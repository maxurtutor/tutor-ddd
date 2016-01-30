package org.maxur.ddd.domain;

import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.maxur.ddd.domain.ServiceLocatorProvider.service;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class Team extends Entity {

    private String name;

    private Integer maxCapacity;

    private Team(String id, String name, Integer maxCapacity) {
        super(id);
        this.name = name;
        this.maxCapacity = maxCapacity;
    }

    public Team(String name, Integer maxCapacity) {
        this.name = name;
        this.maxCapacity = maxCapacity;
    }

    public static Team newTeam(String name, Integer maxCapacity) throws BusinessException {
        return new Team(
                checkName(name),
                checkMaxCapacity(maxCapacity)
        );
    }

    public static Team restore(Snapshot snapshot) throws BusinessException {
        return new Team(
                checkId(snapshot.getId()),
                checkName(snapshot.getName()),
                checkMaxCapacity(snapshot.getMaxCapacity())
        );
    }

    private static String checkId(String id) throws BusinessException {
        if (isNullOrEmpty(id)) {
            throw new BusinessException("Team Id must not be empty");
        }
        return id;
    }

    private static String  checkName(String name) throws BusinessException {
        if (isNullOrEmpty(name)) {
            throw new BusinessException("Team name must not be empty");
        }
        return name;
    }

    private static Integer checkMaxCapacity(Integer maxCapacity) throws BusinessException {
        if (maxCapacity < 1) {
            throw new BusinessException("Team max Capacity must be more than 0");
        }
        return maxCapacity;
    }

    public void checkTeamCapacity() throws BusinessException {
        Integer count = service(UserDao.class).findCountByTeam(getId().asString());
        if (Objects.equals(count, maxCapacity)) {
            throw new BusinessException("The limit users in team is exceeded");
        }
    }

    public Team.Snapshot getSnapshot() {
        final Snapshot snapshot = new Snapshot();
        snapshot.setId(getId().asString());
        snapshot.setName(this.name);
        snapshot.setMaxCapacity(this.maxCapacity);
        return snapshot;
    }

    @SuppressWarnings("WeakerAccess")
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
