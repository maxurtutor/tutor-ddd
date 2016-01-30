package org.maxur.ddd.domain;

import java.util.List;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>28.01.2016</pre>
 */
public interface UserDao {

    void insert(User.Snapshot user);

    void delete(String id);

    void update(User.Snapshot user);

    void changePassword(String id, String password);

    User findById(String id);

    List<User> findAll();

    Integer findCountByTeam(String id);
}
