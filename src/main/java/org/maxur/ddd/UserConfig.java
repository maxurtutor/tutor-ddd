package org.maxur.ddd;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.07.2016</pre>
 */
@Data
public class UserConfig {
    @NotNull
    private Date released;
    @NotNull
    private String version;

    private RestConfig rest;
    @NotNull
    private JdbcConfig jdbc;
}
