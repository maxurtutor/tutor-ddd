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
public class Group extends Entity {

    private String name;

    private Integer maxCapacity;

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

    public boolean isCompleted(DBI dbi) {
        Integer countByGroup = dbi.onDemand(UserDAO.class).findCountByGroup(getId());
        return  Objects.equals(countByGroup, getMaxCapacity());
    }

    public void add(User user, DBI dbi, Notification notification){
        if (isCompleted(dbi)) {
            notification.addError("More users than allowed in group");
            return;
        }
        dbi.onDemand(AccountDao.class).save(user, this);
    }
}
