package org.maxur.mserv.config;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.07.2016</pre>
 */
public class RestConfig {

    @Getter
    @Setter
    private String url = "http://localhost:8080";

    public URI uri() {
        return URI.create(url);
    }

    @Override
    public String toString() {
        return url;
    }
}
