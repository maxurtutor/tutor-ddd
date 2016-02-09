package org.maxur.ddd.dao;

import org.maxur.ddd.domain.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */

public abstract class UserDao implements GetHandle {

    public Set<User> findAll() {
        return getHandle().attach(UserQueryDao.class).findAll();
    }

    public User findById(String id) {
        final Iterator<User> iterator = getHandle().attach(UserQueryDao.class).findById(id).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    public User findByEmail(String email) {
        final Iterator<User> iterator = getHandle().attach(UserQueryDao.class).findByEmail(email).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }


    @RegisterMapper(UserQueryDao.Mapper.class)
    static interface UserQueryDao {
        @SqlQuery(
                "SELECT\n" +
                        "  u.user_id,\n" +
                        "  email,\n" +
                        "  password,\n" +
                        "  role_id\n" +
                        "FROM t_user u LEFT JOIN t_user_role r ON u.user_id = r.user_id \n"
        )
        Set<User> findAll();

        @SqlQuery("SELECT\n" +
                "  u.user_id,\n" +
                "  email,\n" +
                "  password,\n" +
                "  role_id\n" +
                "FROM t_user u LEFT JOIN t_user_role r ON u.user_id = r.user_id\n" +
                "WHERE u.user_id = :id")
        Set<User> findById(@Bind("id") String id);

        @SqlQuery("SELECT\n" +
                "  u.user_id,\n" +
                "  email,\n" +
                "  password,\n" +
                "  role_id\n" +
                "FROM t_user u LEFT JOIN t_user_role r ON u.user_id = r.user_id\n" +
                "WHERE email = :email")
        Set<User> findByEmail(@Bind("email") String email);

        class Mapper implements ResultSetMapper<User> {

            Map<String, User> users = new HashMap<>();

            public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                final String email = r.getString("email");
                final String password = r.getString("password");
                final User user = users.computeIfAbsent(r.getString("user_id"), (id) -> new User(id, email, password));
                final String role = r.getString("role_id");
                if (role != null) {
                    user.addRole(role);
                }
                return user;
            }
        }
    }


}