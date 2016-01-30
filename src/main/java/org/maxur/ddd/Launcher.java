package org.maxur.ddd;

import com.codahale.metrics.JmxReporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.UrlConfigurationSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.h2.tools.RunScript;
import org.maxur.ddd.domain.AccountDao;
import org.maxur.ddd.domain.ServiceLocatorProvider;
import org.maxur.ddd.domain.TeamDao;
import org.maxur.ddd.domain.UserDao;
import org.maxur.ddd.infrastructure.dao.AccountDaoJdbiImpl;
import org.maxur.ddd.infrastructure.dao.TeamDaoJdbiImpl;
import org.maxur.ddd.infrastructure.dao.UserDaoJdbiImpl;
import org.maxur.ddd.infrastructure.mail.MailServiceJavaxImpl;
import org.maxur.ddd.infrastructure.view.BusinessExceptionHandler;
import org.maxur.ddd.infrastructure.view.RuntimeExceptionHandler;
import org.maxur.ddd.infrastructure.view.UserResource;
import org.maxur.ddd.service.*;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.sql.SQLException;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
public class Launcher extends Application<Launcher.AppConfiguration> {

    public static void main(String[] args) throws Exception {
        URL resource = Launcher.class.getClassLoader().getResource("tddd.yml");
        if (resource == null) {
            return;
        }
        new Launcher().run("server", resource.toString());
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new UrlConfigurationSourceProvider());
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
    }

    @Override
    public void run(final AppConfiguration cfg, final Environment env) throws IOException, SQLException {
        JmxReporter.forRegistry(env.metrics()).build().start();
        DBI dbi = makeDBI(cfg, env);
        final MailService sender = new MailServiceJavaxImpl("nobody@mail.org");
        AbstractBinder binder = makeBinder(env, dbi, sender);
        initRest(env.jersey(), binder);
    }

    private DBI makeDBI(AppConfiguration cfg, Environment env) throws IOException, SQLException {
        DBI dbi = new DBIFactory().build(env, cfg.getDataSourceFactory(), "db");
        initDB(dbi);
        return dbi;
    }

    private void initDB(DBI dbi) throws IOException, SQLException {
        try (Handle h = dbi.open()) {
            runScript(h, "/db.ddl");
            runScript(h, "/test.dml");
        }
    }

    private void runScript(Handle h, String script) throws IOException, SQLException {
        try (
                InputStream is = getClass().getResourceAsStream(script);
                Reader reader = new InputStreamReader(is)
        ) {
            RunScript.execute(h.getConnection(), reader);
        }
    }

    private AbstractBinder makeBinder(Environment env, DBI dbi, MailService sender) {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bind(env.lifecycle()).to(LifecycleEnvironment.class);
                bind(ServiceLocatorProvider.class).to(ServiceLocatorProvider.class).in(Singleton.class);
                bind(AccountService.class).to(AccountService.class).in(Singleton.class);
                bindFactory(daoFactory(dbi, AccountDaoJdbiImpl.class)).to(AccountDao.class);
                bindFactory(daoFactory(dbi, UserDaoJdbiImpl.class)).to(UserDao.class);
                bindFactory(daoFactory(dbi, TeamDaoJdbiImpl.class)).to(TeamDao.class);
                bind(sender).to(MailService.class);
            }
        };
    }

    private <T> Factory<T> daoFactory(final DBI dbi, final Class<T> jdbiClass) {
        return new DaoFactory<>(dbi, jdbiClass);
    }

    private void initRest(JerseyEnvironment jersey, AbstractBinder binder) {
        jersey.register(RuntimeExceptionHandler.class);
        jersey.register(BusinessExceptionHandler.class);
        jersey.packages(UserResource.class.getPackage().getName());
        jersey.register(binder);
    }

    static class AppConfiguration extends Configuration {
        @Valid
        @NotNull
        @JsonProperty
        private DataSourceFactory database = new DataSourceFactory();

        DataSourceFactory getDataSourceFactory() {
            return database;
        }
    }

    private static class DaoFactory<T> implements Factory<T> {

        private final DBI dbi;

        private final Class<T> jdbiClass;

        DaoFactory(DBI dbi, Class<T> jdbiClass) {
            this.dbi = dbi;
            this.jdbiClass = jdbiClass;
        }

        @Override
        public T provide() {
            return dbi.onDemand(jdbiClass);
        }

        @Override
        public void dispose(T instance) {

        }
    }
}
