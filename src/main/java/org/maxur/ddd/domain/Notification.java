package org.maxur.ddd.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>13.12.2015</pre>
 */
public class Notification {

    private final List<String> errors = new ArrayList<>();

    public void addError(String message) {
        errors.add(message);
    }

    public boolean hasErrors() {
        return ! errors.isEmpty();
    }

    public String errorMessage() {
        return errors.stream()
                .collect(Collectors.joining(", "));
    }
}
