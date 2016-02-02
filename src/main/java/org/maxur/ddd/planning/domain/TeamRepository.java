package org.maxur.ddd.planning.domain;

import org.maxur.ddd.commons.domain.Entity;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>28.01.2016</pre>
 */
public interface TeamRepository {

    Team findById(String id);

    void insert(Entity team);
}
