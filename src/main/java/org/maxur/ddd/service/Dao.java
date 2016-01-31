package org.maxur.ddd.service;

import org.maxur.ddd.domain.Entity;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>31.01.2016</pre>
 */
public interface Dao {

    void insert(Entity e);

    void update(Entity e);

    void delete(Entity e);
}
