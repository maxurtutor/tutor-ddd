package org.maxur.ddd.dao;

import org.maxur.ddd.domain.Group;
import org.maxur.ddd.domain.User;
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
@RegisterMapper(GroupDAO.Mapper.class)
public interface GroupDAO {

    @SqlQuery("SELECT * \n" +
            "FROM t_group\n" +
            "WHERE group_id = :id")
    Group findById(@Bind("id") String id);

    @SqlUpdate("UPDATE t_group \n" +
            "SET \n" +
            "  group_name = :name, \n" +
            "  max_capacity = :maxCapacity, \n" +
            "  update_date = CURRENT_TIMESTAMP()\n" +
            "WHERE group_id = :id;\n")
    void update(@BindBean Group group);

    class Mapper implements ResultSetMapper<Group> {
        public Group map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            final Group result = new Group(r.getString("group_id"));
            result.setName(r.getString("group_name"));
            result.setMaxCapacity(r.getInt("max_capacity"));
            return result;
        }
    }
}