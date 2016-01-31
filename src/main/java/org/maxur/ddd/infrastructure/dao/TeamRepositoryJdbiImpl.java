package org.maxur.ddd.infrastructure.dao;

import org.maxur.ddd.domain.BusinessException;
import org.maxur.ddd.domain.Entity;
import org.maxur.ddd.domain.Team;
import org.maxur.ddd.domain.TeamRepository;
import org.maxur.ddd.service.Dao;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
@RegisterMapper(TeamRepositoryJdbiImpl.Mapper.class)
public interface TeamRepositoryJdbiImpl extends TeamRepository, Dao {

    @Override
    @SqlQuery("SELECT * \n" +
            "FROM t_team\n" +
            "WHERE team_id = :id")
    Team findById(@Bind("id") String id);

    @Override
    @SqlUpdate("UPDATE t_team \n" +
            "SET \n" +
            "  team_name = :name, \n" +
            "  max_capacity = :maxCapacity, \n" +
            "  update_date = CURRENT_TIMESTAMP\n" +
            "WHERE team_id = :id;\n")
    void update(@BindTeam Entity team);

    @Override
    @SqlUpdate ("INSERT INTO t_team (team_id, team_name, max_capacity) VALUES (:id, :name, :maxCapacity);")
    void insert(@BindTeam Entity team);

    class Mapper implements ResultSetMapper<Team> {
        public Team map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            Team.Snapshot snapshot = new Team.Snapshot();
            snapshot.setId(r.getString("team_id"));
            snapshot.setName(r.getString("team_name"));
            snapshot.setMaxCapacity(r.getInt("max_capacity"));
            try {
                return Team.restore(snapshot);
            } catch (BusinessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @BindingAnnotation(BindTeam.TeamBinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface BindTeam {
        class TeamBinderFactory implements BinderFactory {
            public Binder build(Annotation annotation) {
                return new Binder<BindTeam, Team>() {
                    public void bind(SQLStatement binder, BindTeam bind, Team team) {
                        binder.bindFromProperties(team.getSnapshot());
                    }
                };
            }
        }
    }

}