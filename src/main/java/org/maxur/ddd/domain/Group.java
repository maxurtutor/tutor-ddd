package org.maxur.ddd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.maxur.ddd.dao.AccountDao;
import org.maxur.ddd.dao.UserDAO;
import org.skife.jdbi.v2.DBI;

import java.util.Objects;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
@SuppressWarnings("unused")
public class Group extends Entity {

    @JsonProperty
    private String name;

    @JsonProperty
    private Integer maxCapacity;

    public Group() {
    }

    public Group(String id) {
        super(id);
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

    public boolean isCompleted(DBI dbi) throws ValidationException {
        Integer countByGroup = dbi.onDemand(UserDAO.class).findCountByGroup(getId());
        return  Objects.equals(countByGroup, getMaxCapacity());
    }

    public void add(User user, DBI dbi) throws ValidationException {
        if (isCompleted(dbi)) {
            throw new ValidationException("More users than allowed in group");
        }
        dbi.onDemand(AccountDao.class).save(user, this);
    }
}
