package org.maxur.ddd.commons.service;

import org.maxur.ddd.commons.Dao;
import org.maxur.ddd.domain.BusinessException;
import org.maxur.ddd.domain.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UnitOfWorkImpl {

    private Map<Class<? extends Entity>, Dao> daos = new HashMap<>();

    public UnitOfWorkImpl() {
    }

    protected void insert(Class<Entity> entityClass, List<Entity> entities) {
        Dao dao = getDaoFor(entityClass);
        // TODO Can be batch
        entities.forEach(dao::insert);
    }

    protected void update(Class<Entity> entityClass, List<Entity> entities) {
        Dao dao = getDaoFor(entityClass);
        // TODO Can be batch
        entities.forEach(dao::update);
    }

    protected void delete(Class<Entity> entityClass, List<Entity> entities) {
        Dao dao = getDaoFor(entityClass);
        // TODO Can be batch
        entities.forEach(dao::delete);
    }

    private Dao getDaoFor(Class<? extends Entity> entityClass) {
        Dao dao = daos.get(entityClass);
        if (dao == null) {
            daos.put(entityClass, dao = makeDaoFor(entityClass));
        }
        return dao;
    }

    protected abstract Dao makeDaoFor(Class<? extends Entity> entityClass);

    protected abstract void commit(Runnable runnable)
            throws BusinessException;
}