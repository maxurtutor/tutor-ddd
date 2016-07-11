package org.maxur.ddd;

import io.baratine.service.Result;
import io.baratine.service.Service;
import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static org.maxur.ddd.data.sql.Tables.TASK;

/**
 * The type Task service.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>10.07.2016</pre>
 */
@SuppressWarnings("WeakerAccess")
@Service
public class TaskService {

    @Inject
    private DataSource dataSource;

    /**
     * Gets all.
     *
     * @param result the result
     */
    public void getAll(Result<List<Task>> result) {
        try(DSLContext create = context()) {
            final List<Task> tasks = create
                .select(TASK.ID, TASK.SUMMARY, TASK.DESCRIPTION)
                .from(TASK)
                .fetchInto(Task.class);
            result.ok(tasks);
        }
    }

    @SneakyThrows
    private DSLContext context() {
        final Connection connection = dataSource.getConnection();
        return DSL.using(connection, SQLDialect.H2);
    }
}
