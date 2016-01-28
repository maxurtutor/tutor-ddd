package org.maxur.ddd.infrastructure.dao;

import org.maxur.ddd.domain.Team;
import org.maxur.ddd.service.TeamDao;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
@RegisterMapper(TeamDaoJdbiImpl.Mapper.class)
public interface TeamDaoJdbiImpl extends TeamDao {

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
    void update(@BindBean Team team);

    class Mapper implements ResultSetMapper<Team> {
        public Team map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            final Team team = new Team();
            team.setId(r.getString("team_id"));
            team.setName(r.getString("team_name"));
            team.setMaxCapacity(r.getInt("max_capacity"));
            return team;
        }
    }

}