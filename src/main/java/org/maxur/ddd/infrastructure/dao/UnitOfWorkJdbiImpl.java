package org.maxur.ddd.infrastructure.dao;

import org.maxur.ddd.domain.BusinessException;
import org.maxur.ddd.domain.Entity;
import org.maxur.ddd.domain.Team;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.service.Dao;
import org.maxur.ddd.service.UnitOfWorkImpl;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>31.01.2016</pre>
 */
public abstract class UnitOfWorkJdbiImpl extends UnitOfWorkImpl implements GetHandle {

    private Handle handle;

    @Override
    protected Dao makeDaoFor(Class<? extends Entity> entityClass) {
        if (entityClass == User.class ) {
            return handle.attach(UserRepositoryJdbiImpl.class);
        }
        if (entityClass == Team.class ) {
            return handle.attach(TeamRepositoryJdbiImpl.class);
        }
        throw new IllegalStateException(format("Dao for class '%s' is not found", entityClass));
    }

    @Override
    @Transaction
    public void commit(Runnable runnable) throws BusinessException {
        handle = getHandle();
        runnable.run();
    }
}
