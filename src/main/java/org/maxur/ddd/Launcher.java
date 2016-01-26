package org.maxur.ddd;

import com.codahale.metrics.JmxReporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
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
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.h2.tools.RunScript;

import org.maxur.ddd.domain.MailService;
import org.maxur.ddd.domain.Notifier;
import org.maxur.ddd.domain.TeamRepository;
import org.maxur.ddd.domain.UserRepository;
import org.maxur.ddd.service.*;
import org.maxur.ddd.view.RuntimeExceptionHandler;
import org.maxur.ddd.view.UserResource;
import org.maxur.ddd.view.BusinessExceptionHandler;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

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
        final EventBus events = makeEventBus();
        final MailService sender = new MailServiceJavaxImpl("nobody@mail.org");
        AbstractBinder binder = makeBinder(env, dbi, sender, events);
        initRest(env.jersey(), binder);
    }

    private EventBus makeEventBus() {
        final ThreadPoolExecutor executor =
                new ThreadPoolExecutor(1, 1, 0L, MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        final AsyncEventBus events = new AsyncEventBus("events", executor);
        events.register(new DeadEventsSubscriber());
        return events;
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

    private AbstractBinder makeBinder(Environment env, DBI dbi, MailService sender, final EventBus events) {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bind(env.lifecycle()).to(LifecycleEnvironment.class);
                bind(dbi).to(DBI.class);
                bind(AccountService.class).to(AccountService.class).in(Singleton.class);
                bind(UserRepository.class).to(UserRepository.class).in(Singleton.class);
                bind(TeamRepository.class).to(TeamRepository.class).in(Singleton.class);
                bind(CommandHandler.class).to(CommandHandler.class).in(Singleton.class);
                bind(sender).to(MailService.class);
                bind(events).to(EventBus.class);
                bind(new Notifier(sender, events)).to(Notifier.class);
            }
        };
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

}
