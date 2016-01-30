package org.maxur.ddd.infrastructure.dao;

import org.maxur.ddd.domain.Team;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.domain.AccountDao;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>11.11.2015</pre>
 */
public abstract class AccountDaoJdbiImpl implements GetHandle, AccountDao {

    @Override
    @Transaction
    public void save(User user, Team team) {
        final Handle handle = getHandle();
        handle.attach(UserDaoJdbiImpl.class).insert(user.getSnapshot());
        handle.attach(TeamDaoJdbiImpl.class).update(team.getSnapshot());
    }

    @Override
    @Transaction
    public void update(User user, Team team) {
        final Handle handle = getHandle();
        handle.attach(UserDaoJdbiImpl.class).update(user.getSnapshot());
        handle.attach(TeamDaoJdbiImpl.class).update(team.getSnapshot());
    }

    @Override
    @Transaction
    public void delete(User user, Team team) {
        final Handle handle = getHandle();
        handle.attach(UserDaoJdbiImpl.class).delete(user.getId());
        handle.attach(TeamDaoJdbiImpl.class).update(team.getSnapshot());
    }

}
