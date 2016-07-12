package org.maxur.ddd;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The type Task.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>10.07.2016</pre>
 */
@ToString
@EqualsAndHashCode
public class Task {

    private String summary;

    private String description;

    public Task() {
    }

    /**
     * Instantiates a new Task.
     *
     * @param summary the summary
     */
    public Task(final String summary) {
        this.summary = summary;
    }

}
