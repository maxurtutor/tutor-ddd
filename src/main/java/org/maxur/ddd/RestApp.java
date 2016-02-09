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

    private static final AssetsBundle ASSETS_BUNDLE = new AssetsBundle("/assets", "/", "index.html");
    private static final AssetsBundle SWAGGER_BUNDLE = new AssetsBundle("/swagger-ui", "/api-docs", "index.html");


    @Contract(" -> !null")
    static RestApp application() {
        return new RestApp();
    }

    @SuppressWarnings("WeakerAccess")
    public RestApp() {
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new UrlConfigurationSourceProvider());
        bootstrap.addBundle(ASSETS_BUNDLE);
        bootstrap.addBundle(SWAGGER_BUNDLE);
    }

    @Override
    public void run(final Config cfg, final Environment env) {
        JmxReporter.forRegistry(env.metrics()).build().start();
        binder(cfg, env);
        initRest(cfg, env);
        initDatabase(cfg);
    }

    private void initDatabase(Config cfg)  {
        try {
            runScripts(cfg.getScripts());
        } catch (IOException | SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
