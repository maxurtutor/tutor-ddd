package org.maxur.ddd.infrastructure.dao;

import org.maxur.ddd.domain.BusinessException;
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
    void update(@BindBean Team.Snapshot team);

    class Mapper implements ResultSetMapper<Team> {
        public Team map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            final Team.Snapshot snapshot = new Team.Snapshot();
            snapshot.id = r.getString("team_id");
            snapshot.name = r.getString("team_name");
            snapshot.maxCapacity = r.getInt("max_capacity");
            try {
                return Team.restore(snapshot);
            } catch (BusinessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}