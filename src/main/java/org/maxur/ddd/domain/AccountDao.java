package org.maxur.ddd.domain;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>28.01.2016</pre>
 */
public interface AccountDao {

    void save(User user, Team team);

    void update(User user, Team team);

    void delete(User user, Team team);
}
