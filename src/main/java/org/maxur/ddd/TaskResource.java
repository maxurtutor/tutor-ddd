package org.maxur.ddd;

import io.baratine.service.Result;
import io.baratine.service.Service;
import io.baratine.web.Get;

import javax.inject.Inject;
import java.util.List;

/**
 * The type Hello.
 */
@SuppressWarnings("WeakerAccess")
public class TaskResource {

    @Inject @Service
    private TaskService taskService;

    /**
     * Get all Tasks.
     *
     * @param result the result
     */
    @Get("task")
    public void all(Result<List<Task>> result) {
        taskService.getAll(result);
    }

}