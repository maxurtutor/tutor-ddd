package org.maxur.ddd;

import io.baratine.inject.Bean;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>10.07.2016</pre>
 */
@SuppressWarnings("WeakerAccess")
public class SysConfig {

    @Inject
    private UserConfig config;

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Bean
    public DataSource dataSource() {
        return config.getJdbc().dataSource();
    }

}
