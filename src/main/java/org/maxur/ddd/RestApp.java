package org.maxur.ddd;

import com.codahale.metrics.JmxReporter;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.UrlConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jetbrains.annotations.Contract;
import org.maxur.ddd.config.Config;

import java.io.IOException;
import java.sql.SQLException;

import static org.maxur.ddd.config.Binder.binder;
import static org.maxur.ddd.config.Rest.initRest;
import static org.maxur.ddd.util.DBUtils.runScripts;


/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/4/2016</pre>
 */
public class RestApp extends Application<Config> {

    public static final AssetsBundle ASSETS_BUNDLE = new AssetsBundle("/assets", "/", "index.html");

    public static final String[] DB_SCRIPTS = {"/db.ddl"};

    @Contract(" -> !null")
    static RestApp application() {
        return new RestApp();
    }

    public RestApp() {
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new UrlConfigurationSourceProvider());
        bootstrap.addBundle(ASSETS_BUNDLE);
    }

    @Override
    public void run(final Config cfg, final Environment env) {
        JmxReporter.forRegistry(env.metrics()).build().start();
        binder(cfg, env);
        initRest(env.jersey());
        initDatabase();
    }

    private void initDatabase()  {
        try {
            runScripts(DB_SCRIPTS);
        } catch (IOException | SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
