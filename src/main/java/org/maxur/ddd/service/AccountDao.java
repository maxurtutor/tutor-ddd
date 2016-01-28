package org.maxur.ddd.service;

import org.maxur.ddd.domain.Team;
import org.maxur.ddd.domain.User;
import org.skife.jdbi.v2.sqlobject.Transaction;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>28.01.2016</pre>
 */
public interface AccountDao {

    void save(User user, Team team);

    void update(User user, Team team);

    void delete(String id, Team team);
}
