package org.maxur.ddd.view;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>11/25/13</pre>
 */
@SuppressWarnings("unused")
@XmlRootElement
public class Incident implements Serializable {

    private static final long serialVersionUID = 2368849548039200044L;

    private String message;

    public Incident() {
    }

    public Incident(final String message) {
        this.message = message;
    }

    public static List<Incident> incidents(final String... messages) {
        return stream(messages).map(Incident::new).collect(toList());
    }

    public String getMessage() {
        return message;
    }
}
