package org.maxur.ddd.config;

import lombok.Data;
import org.maxur.mserv.annotation.Configuration;
import org.maxur.mserv.annotation.Key;
import org.maxur.mserv.config.RestConfig;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.07.2016</pre>
 */
@Data
@Configuration(fileName = "./conf/tddd.yaml")
public class UserConfig {

    @NotNull
    private Date released;

    @NotNull
    private String version;

    @Key("rest")
    private RestConfig rest;

    @NotNull
    @Key("jdbc")
    private JdbcConfig jdbc;
}
