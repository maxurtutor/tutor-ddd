package org.maxur.ddd.config;

import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.servlet.ServletProperties;
import org.maxur.ddd.dao.UserDao;
import org.maxur.ddd.service.AccountService;
import org.maxur.ddd.service.components.LoggerFactory;
import org.maxur.ddd.ui.components.BaseAuthenticator;
import org.skife.jdbi.v2.DBI;

import javax.inject.Singleton;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>02.02.2016</pre>
 */
public final class Binder extends AbstractBinder {

    private final Environment env;

    private final DBI dbi;

    public static AbstractBinder binder(Config cfg, Environment env) {
        ServiceLocatorFactory locatorFactory = ServiceLocatorFactory.getInstance();
        ServiceLocator locator = locatorFactory.create("ServiceLocator");
        new ServiceLocatorProvider(locator);
        env.getApplicationContext().getAttributes().setAttribute(ServletProperties.SERVICE_LOCATOR, locator);
        Binder binder = new Binder(cfg, env);
        ServiceLocatorUtilities.bind(locator, binder);
        return binder;
    }

    private Binder(Config cfg, Environment env) {
        this.env = env;
        this.dbi = new DBIFactory().build(env, cfg.getDataSourceFactory(), "db");
    }

    @SuppressWarnings("RedundantToBinding")
    @Override
    protected void configure() {
        bind(env.lifecycle()).to(LifecycleEnvironment.class);
        bind(dbi).to(DBI.class);
        bind(LoggerFactory.class).to(LoggerFactory.class).in(Singleton.class);
        bind(BaseAuthenticator.class).to(BaseAuthenticator.class).in(Singleton.class);
        bind(AccountService.class).to(AccountService.class).in(Singleton.class);
        bindFactory(daoFactory(dbi, UserDao.class)).to(UserDao.class);
    }

    private <T> Factory<T> daoFactory(final DBI dbi, final Class<T> jdbiClass) {
        final DaoFactory<T> factory = new DaoFactory<>(dbi, jdbiClass);
        ServiceLocatorProvider.inject(factory);
        return factory;
    }

}
