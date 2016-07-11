package org.maxur.ddd;

import lombok.Data;

import java.util.Date;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.07.2016</pre>
 */
@Data
public class UserConfig {
    private Date released;
    private String version;
    private RestConfig rest;
    private JdbcConfig jdbc;
}
