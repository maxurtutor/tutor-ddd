package org.maxur.ddd.infrastructure.dao;

import org.maxur.ddd.domain.BusinessException;
import org.maxur.ddd.domain.Entity;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.domain.UserRepository;
import org.maxur.ddd.service.Dao;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
@RegisterMapper(UserRepositoryJdbiImpl.Mapper.class)
public interface UserRepositoryJdbiImpl extends UserRepository, Dao {

    @SqlUpdate("INSERT INTO t_user (\n" +
            "  user_id, \n" +
            "  user_name, \n" +
            "  first_name, \n" +
            "  last_name, \n" +
            "  email, \n" +
            "  password,\n" +
            "  team_id\n" +
            ") VALUES (:id, :name, :firstName, :lastName, :email, :password, :teamId)")
    void insert(@BindUser Entity user);

    @SqlUpdate("DELETE FROM t_user\n" +
            "WHERE user_id = :id")
    void delete(@BindUser Entity user);

    @SqlUpdate("UPDATE t_user SET    \n" +
            "  user_name = :name, \n" +
            "  first_name = :firstName, \n" +
            "  last_name = :lastName, \n" +
            "  email = :email, \n" +
            "  password = :password, \n" +
            "  team_id = :teamId\n" +
            "WHERE\n" +
            "  user_id = :id")
    void update(@BindUser Entity user);

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

    @BindingAnnotation(BindUser.UserBinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface BindUser {
        class UserBinderFactory implements BinderFactory {
            public Binder build(Annotation annotation) {
                return new Binder<BindUser, User>() {
                    public void bind(SQLStatement binder, BindUser bind, User user) {
                        binder.bindFromProperties(user.getSnapshot());
                    }
                };
            }
        }
    }

}