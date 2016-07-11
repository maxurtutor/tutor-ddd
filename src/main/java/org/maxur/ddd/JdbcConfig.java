package org.maxur.ddd;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;

/**
 * The type Jdbc config.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>12.07.2016</pre>
 */
public class JdbcConfig {

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private String user;

    @Getter
    @Setter
    private String password;

    /**
     * Data source data source.
     *
     * @return the data source
     */
    public DataSource dataSource() {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }

    @Override
    public String toString() {
        return String.format("%s:%s url: %s", user, password, url);
    }
}
