package org.maxur.ddd.domain;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>28.01.2016</pre>
 */
public interface TeamRepository {

    Team findById(String id);

    void insert(Entity team);
}
