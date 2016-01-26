package org.maxur.ddd.domain;

import org.maxur.ddd.dao.AccountDao;
import org.maxur.ddd.dao.UserDao;
import org.skife.jdbi.v2.DBI;

import javax.inject.Inject;
import java.util.List;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.01.2016</pre>
 */
public class UserRepository {

    private final DBI dbi;

    @Inject
    public UserRepository(DBI dbi) {
        this.dbi = dbi;
    }

    public User findById(String id) {
        return dbi.onDemand(UserDao.class).findById(id);
    }

    public List<User> findAll() {
        return dbi.onDemand(UserDao.class).findAll();
    }

    public void update(User user, Team team) {
        dbi.onDemand(AccountDao.class).update(user, team);
    }
}
