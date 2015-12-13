package org.maxur.ddd.dao;

import org.maxur.ddd.domain.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
@RegisterMapper(UserDAO.Mapper.class)
public interface UserDAO {

    @SqlUpdate("INSERT INTO t_user (\n" +
            "  user_id, \n" +
            "  user_name, \n" +
            "  first_name, \n" +
            "  last_name, \n" +
            "  email, \n" +
            "  group_id\n" +
            ") VALUES (:id, :name, :firstName, :lastName, :email, :groupId)")
    void insert(@BindBean User user);

    @SqlQuery("SELECT * \n" +
            "FROM t_user\n" +
            "  JOIN t_group ON t_user.group_id = t_group.group_id\n" +
            "WHERE user_id = :id")
    User findById(@Bind("id") String id);

    @SqlQuery("SELECT * \n" +
            "FROM t_user\n" +
            "  JOIN t_group ON t_user.group_id = t_group.group_id")
    List<User> findAll();

    @SqlQuery("SELECT COUNT(*) \n" +
            "FROM t_user\n" +
            "WHERE group_id = :id")
    Integer findCountByGroup(@Bind("id") String id);

    class Mapper implements ResultSetMapper<User> {
        public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return User.user(
                    r.getString("user_id"),
                    r.getString("user_name"),
                    r.getString("first_name"),
                    r.getString("last_name"),
                    r.getString("email"),
                    r.getString("group_id"),
                    r.getString("group_name")
            );
        }
    }
}