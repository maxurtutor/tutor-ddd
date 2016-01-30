package org.maxur.ddd.domain;

import org.maxur.ddd.service.UserDao;

import java.util.Objects;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
@SuppressWarnings({"unused"})
public class Team {

    private String id;

    private String name;

    private Integer maxCapacity;

    public Team() {
    }

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

    void checkTeamCapacity(UserDao userDao) throws BusinessException {
        Integer count = userDao.findCountByTeam(id);
        if (Objects.equals(count, maxCapacity)) {
            throw new BusinessException("The limit users in team is exceeded");
        }
    }
}
