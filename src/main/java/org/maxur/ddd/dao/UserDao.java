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
@RegisterMapper(UserDao.Mapper.class)
public interface UserDao {

    @SqlUpdate("INSERT INTO t_user (\n" +
            "  user_id, \n" +
            "  user_name, \n" +
            "  first_name, \n" +
            "  last_name, \n" +
            "  email, \n" +
            "  password,\n" +
            "  team_id\n" +
            ") VALUES (:id, :name, :firstName, :lastName, :email, :password, :teamId)")
    void insert(@BindBean User user);

    @SqlUpdate("DELETE FROM t_user\n" +
            "WHERE user_id = :id")
    void delete(@Bind("id") String id);

    @SqlUpdate("UPDATE t_user SET    \n" +
            "  user_name = :name, \n" +
            "  first_name = :firstName, \n" +
            "  last_name = :lastName, \n" +
            "  email = :email, \n" +
            "  password = :password, \n" +
            "  team_id = :teamId\n" +
            "WHERE\n" +
            "  user_id = :id")
    void update(@BindBean User user);

    @SqlUpdate("UPDATE t_user SET    \n" +
            "  password = :password \n" +
            "WHERE\n" +
            "  user_id = :id")
    void changePassword(@Bind("id") String id, @Bind("password") String password);

    @SqlQuery("SELECT * \n" +
            "FROM t_user\n" +
            "  JOIN t_team ON t_user.team_id = t_team.team_id\n" +
            "WHERE user_id = :id")
    User findById(@Bind("id") String id);

    @SqlQuery("SELECT * \n" +
            "FROM t_user\n" +
            "  JOIN t_team ON t_user.team_id = t_team.team_id")
    List<User> findAll();

    @SqlQuery("SELECT COUNT(*) \n" +
            "FROM t_user\n" +
            "WHERE team_id = :id")
    Integer findCountByTeam(@Bind("id") String id);

    class Mapper implements ResultSetMapper<User> {
        public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            final User user = new User();
            user.setId(r.getString("user_id"));
            user.setName(r.getString("user_name"));
            user.setFirstName(r.getString("first_name"));
            user.setLastName(r.getString("last_name"));
            user.setEmail(r.getString("email"));
            user.setTeamId(r.getString("team_id"));
            user.setTeamName(r.getString("team_name"));
            user.setPassword(r.getString("password"));
            return user;
        }
    }
}