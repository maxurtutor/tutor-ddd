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
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.servlet.ServletProperties;
import org.h2.tools.RunScript;
import org.maxur.ddd.domain.*;
import org.maxur.ddd.infrastructure.dao.AccountDaoJdbiImpl;
import org.maxur.ddd.infrastructure.dao.TeamDaoJdbiImpl;
import org.maxur.ddd.infrastructure.dao.UserDaoJdbiImpl;
import org.maxur.ddd.infrastructure.mail.MailServiceJavaxImpl;
import org.maxur.ddd.infrastructure.view.BusinessExceptionHandler;
import org.maxur.ddd.infrastructure.view.RuntimeExceptionHandler;
import org.maxur.ddd.infrastructure.view.UserResource;
import org.maxur.ddd.service.AccountService;
import org.maxur.ddd.service.MailService;
import org.maxur.ddd.service.PlanningService;
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

import static org.glassfish.hk2.utilities.ServiceLocatorUtilities.bind;
import static org.maxur.ddd.domain.EmailAddress.email;
import static org.maxur.ddd.domain.Id.id;
import static org.maxur.ddd.domain.Person.person;

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

        final ServiceLocatorFactory locatorFactory = ServiceLocatorFactory.getInstance();
        final ServiceLocator locator = locatorFactory.create("TdddLocator");
        new ServiceLocatorProvider(locator);
        bind(locator, makeBinder(dbi, sender, env.lifecycle()));
        env.getApplicationContext().getAttributes().setAttribute(ServletProperties.SERVICE_LOCATOR, locator);

        initRest(env.jersey());
        loadData(locator);
    }

    private void loadData(ServiceLocator locator) {
        AccountService accountService = locator.getService(AccountService.class);
        PlanningService planningService = locator.getService(PlanningService.class);
        try {
            Team tddd = planningService.createTeam("T_DDD", 5);
            Team tcqrs = planningService.createTeam("T_CQRS", 3);
            accountService.createUserBy(
                    "iv",
                    person("Ivan", "Ivanov", email("ivan@mail.com")), id(tddd.getId().toString())
            );
            accountService.createUserBy(
                    "petr",
                    person("Petr", "Petrov", email("petr@mail.com")), id(tddd.getId().toString())
            );
            accountService.createUserBy(
                    "sidor",
                    person("Sidor", "Sidorov", email("sidor@mail.com")), id(tcqrs.getId().toString())
            );
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    private DBI makeDBI(AppConfiguration cfg, Environment env) throws IOException, SQLException {
        DBI dbi = new DBIFactory().build(env, cfg.getDataSourceFactory(), "db");
        try (Handle h = dbi.open()) {
            runScript(h, "/db.ddl");
        }
        return dbi;
    }

    private void runScript(Handle h, String script) throws IOException, SQLException {
        try (
                InputStream is = getClass().getResourceAsStream(script);
                Reader reader = new InputStreamReader(is)
        ) {
            RunScript.execute(h.getConnection(), reader);
        }
    }

    private AbstractBinder makeBinder(DBI dbi, MailService sender, LifecycleEnvironment lifecycle) {

        return new AbstractBinder() {
            @Override
            protected void configure() {
                bind(lifecycle).to(LifecycleEnvironment.class);
                bind(AccountService.class).to(AccountService.class).in(Singleton.class);
                bind(PlanningService.class).to(PlanningService.class).in(Singleton.class);
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

    private void initRest(JerseyEnvironment jersey) {
        jersey.register(RuntimeExceptionHandler.class);
        jersey.register(BusinessExceptionHandler.class);
        jersey.packages(UserResource.class.getPackage().getName());
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
