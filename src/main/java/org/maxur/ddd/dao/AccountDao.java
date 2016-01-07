package org.maxur.ddd.dao;

import org.maxur.ddd.domain.Team;
import org.maxur.ddd.domain.User;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>11.11.2015</pre>
 */
public abstract class AccountDao implements GetHandle {

    @Transaction
    public void save(User user, Team team) {
        getHandle().attach(UserDao.class).insert(user);
        getHandle().attach(TeamDao.class).update(team);
    }

}
