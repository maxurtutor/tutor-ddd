package org.maxur.ddd.service;

import org.maxur.ddd.domain.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>28.01.2016</pre>
 */
public interface UserDao {

    void insert(User user);

    void delete(String id);

    void update(User user);

    void changePassword(String id, String password);

    User findById(String id);

    List<User> findAll();

    Integer findCountByTeam(String id);
}
