package org.maxur.ddd.domain;

import java.util.List;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>28.01.2016</pre>
 */
public interface UserDao {

    User findById(String id);

    List<User> findAll();

    Integer findCountByTeam(String id);

    void changePassword(String id, String password);
}
