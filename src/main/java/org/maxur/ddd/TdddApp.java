package org.maxur.ddd;

import com.codahale.metrics.JmxReporter;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.UrlConfigurationSourceProvider;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.config.ScannerFactory;
import io.swagger.jaxrs.config.DefaultJaxrsScanner;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.jersey.listing.ApiListingResourceJSON;
import org.jetbrains.annotations.Contract;
import org.maxur.ddd.config.Binder;
import org.maxur.ddd.config.Config;
import org.maxur.ddd.infrastructure.view.ApiDefinition;
import org.maxur.ddd.infrastructure.view.BusinessExceptionHandler;
import org.maxur.ddd.infrastructure.view.RuntimeExceptionHandler;
import org.maxur.ddd.infrastructure.view.UserResource;

import java.io.IOException;
import java.sql.SQLException;

import static org.maxur.ddd.utils.DBUtils.runScript;

public final class TdddApp extends Application<Config> {

    @Contract(" -> !null")
    static TdddApp application() {
        return new TdddApp();
    }

    public TdddApp() {
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new UrlConfigurationSourceProvider());
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
        bootstrap.addBundle(new AssetsBundle("/swagger-ui", "/api-docs", "index.html"));
    }

    @Override
    public void run(final Config cfg, final Environment env) throws IOException, SQLException {
        JmxReporter.forRegistry(env.metrics()).build().start();
        Binder.make(cfg, env);
        initRest(env.jersey());



        initDatabase();
    }

    private void initDatabase() throws IOException, SQLException {
        runScript("/db.ddl");
       // runScript("/test.dml");
    }

    private void initRest(JerseyEnvironment jersey) {
        jersey.register(RuntimeExceptionHandler.class);
        jersey.register(BusinessExceptionHandler.class);


        // Swagger
        jersey.register(ApiListingResourceJSON.class);
        jersey.register(SwaggerSerializers.class);
        jersey.register(ApiDefinition.class);
        ScannerFactory.setScanner(new DefaultJaxrsScanner());

        jersey.packages(UserResource.class.getPackage().getName());
    }

}