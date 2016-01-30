package org.maxur.ddd.service;

import org.maxur.ddd.domain.Team;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>28.01.2016</pre>
 */
public interface TeamDao {

    Team findById(String id);

    void update(Team.Snapshot team);
}
