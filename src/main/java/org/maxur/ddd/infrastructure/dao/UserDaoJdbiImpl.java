package org.maxur.ddd.infrastructure.dao;

import org.maxur.ddd.domain.BusinessException;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.domain.UserDao;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
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
@RegisterMapper(UserDaoJdbiImpl.Mapper.class)
public interface UserDaoJdbiImpl extends UserDao {

    @SqlUpdate("INSERT INTO t_user (\n" +
            "  user_id, \n" +
            "  user_name, \n" +
            "  first_name, \n" +
            "  last_name, \n" +
            "  email, \n" +
            "  password,\n" +
            "  team_id\n" +
            ") VALUES (:id, :name, :firstName, :lastName, :email, :password, :teamId)")
    void insert(@BindBean User.Snapshot user);

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
    void update(@BindBean User.Snapshot  user);

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
            User.Snapshot snapshot = new User.Snapshot();
            snapshot.setId(r.getString("user_id"));
            snapshot.setName(r.getString("user_name"));
            snapshot.setFirstName(r.getString("first_name"));
            snapshot.setLastName(r.getString("last_name"));
            snapshot.setEmail(r.getString("email"));
            snapshot.setTeamId(r.getString("team_id"));
            snapshot.setTeamName(r.getString("team_name"));
            snapshot.setPassword(r.getString("password"));
            try {
                return User.restore(snapshot);
            } catch (BusinessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}