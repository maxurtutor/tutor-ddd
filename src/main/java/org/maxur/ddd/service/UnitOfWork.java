package org.maxur.ddd.service;

import org.maxur.ddd.domain.BusinessException;
import org.maxur.ddd.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>31.01.2016</pre>
 */
public class UnitOfWork {

    private final Logger logger = LoggerFactory.getLogger(UnitOfWork.class);

    private final List<Entity> inserted = new ArrayList<>();

    private final List<Entity> updated = new ArrayList<>();

    private final List<Entity> deleted = new ArrayList<>();

    private final UnitOfWorkImpl unitOfWorkImpl;

    @Inject
    protected UnitOfWork(UnitOfWorkImpl unitOfWorkImpl) {
        this.unitOfWorkImpl = unitOfWorkImpl;
    }

    public void create(Entity... entity) {
        inserted.addAll(asList(entity));
    }

    public void modify(Entity... entity) {
        updated.addAll(asList(entity));
    }

    public void remove(Entity... entity) {
        deleted.addAll(asList(entity));
    }

    public void commit() throws BusinessException {
        final List<Entity> inserted  = this.inserted;
        final List<Entity> updated  = this.updated;
        final List<Entity> deleted  = this.deleted;
        try {
            unitOfWorkImpl.commit(() -> {
                inserted.stream()
                        .collect(groupingBy(Entity::getEntityClass, mapping(e1 -> e1, toList())))
                        .forEach(unitOfWorkImpl::insert);
                updated.stream()
                        .collect(groupingBy(Entity::getEntityClass, mapping(e1 -> e1, toList())))
                        .forEach(unitOfWorkImpl::update);
                deleted.stream()
                        .collect(groupingBy(Entity::getEntityClass, mapping(e1 -> e1, toList())))
                        .forEach(unitOfWorkImpl::delete);
            });
        } catch (Exception e) {
            logger.error("Unable to persist data: " + e.getMessage());
            throw new BusinessException("Constrains violations");
        }
        clear();
    }

    private void clear() {
        inserted.clear();
        updated.clear();
        deleted.clear();
    }


}
