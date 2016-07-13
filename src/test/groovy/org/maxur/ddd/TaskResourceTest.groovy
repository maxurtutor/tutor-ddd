package org.maxur.ddd

import io.baratine.service.Result
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockConnection
import org.jooq.tools.jdbc.MockDataProvider
import org.jooq.tools.jdbc.MockExecuteContext
import org.jooq.tools.jdbc.MockResult
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.SQLException

import static org.maxur.ddd.data.sql.Tables.TASK

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.07.2016</pre>
 */
class TaskResourceTest extends Specification {

    MockDataProvider provider = new MockDataProvider() {

        @Override
        public MockResult[] execute(MockExecuteContext ctx) throws SQLException {

            DSLContext create = DSL.using(SQLDialect.H2);
            MockResult[] mock = new MockResult[1];


            String sql = ctx.sql();

            if (sql.toUpperCase().startsWith("SELECT")) {

                org.jooq.Result<Record> result = create.newResult(TASK);
                result.add(create.newRecord(TASK));
                result.get(0).setValue(TASK.ID, "1");
                result.get(0).setValue(TASK.SUMMARY, "first");
                mock[0] = new MockResult(1, result);
            }

            return mock;
        }
    }

    def "System should returns all tasks by user request"() {
        given: ""
        TaskService sut = new TaskService();
        Result result = Mock(Result)
        MockConnection connection = new MockConnection(provider);
        sut.dataSource = Mock(DataSource)
        sut.dataSource.getConnection() >> connection
        when: "User navigate to task list"
        sut.getAll(result)
        0 * provider._(*_)
        0 * connection._(*_)
        then: "System returns all tasks"
        1 * result.ok(*_) >> { arguments ->
            final List<Task> tasks = arguments[0]
            assert tasks == [new Task("first")]
        }
    }


}
