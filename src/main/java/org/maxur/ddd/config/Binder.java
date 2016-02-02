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
import org.maxur.ddd.admin.domain.NotificationService;
import org.maxur.ddd.admin.domain.UserRepository;
import org.maxur.ddd.admin.service.AccountService;
import org.maxur.ddd.admin.service.MailService;
import org.maxur.ddd.admin.service.NotificationServiceImpl;
import org.maxur.ddd.commons.domain.ServiceLocatorProvider;
import org.maxur.ddd.commons.service.UnitOfWork;
import org.maxur.ddd.commons.service.UnitOfWorkImpl;
import org.maxur.ddd.infrastructure.dao.DaoFactory;
import org.maxur.ddd.infrastructure.dao.TeamRepositoryJdbiImpl;
import org.maxur.ddd.infrastructure.dao.UnitOfWorkJdbiImpl;
import org.maxur.ddd.infrastructure.dao.UserRepositoryJdbiImpl;
import org.maxur.ddd.infrastructure.mail.MailServiceJavaxImpl;
import org.maxur.ddd.planning.domain.TeamRepository;
import org.maxur.ddd.planning.service.PlanningService;
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

    public static AbstractBinder make(Config cfg, Environment env) {
        ServiceLocatorFactory locatorFactory = ServiceLocatorFactory.getInstance();
        ServiceLocator locator = locatorFactory.create("TdddLocator");
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

    @Override
    protected void configure() {
        bind(env.lifecycle()).to(LifecycleEnvironment.class);
        bind(dbi).to(DBI.class);
        bind(NotificationServiceImpl.class).to(NotificationService.class).in(Singleton.class);
        bind(AccountService.class).to(AccountService.class).in(Singleton.class);
        bind(PlanningService.class).to(PlanningService.class).in(Singleton.class);
        bindFactory(daoFactory(dbi, UserRepositoryJdbiImpl.class)).to(UserRepository.class);
        bindFactory(daoFactory(dbi, TeamRepositoryJdbiImpl.class)).to(TeamRepository.class);
        bind(new MailServiceJavaxImpl("nobody@mail.org")).to(MailService.class);

        bind(UnitOfWork.class).to(UnitOfWork.class);
        bindFactory(daoFactory(dbi, UnitOfWorkJdbiImpl.class)).to(UnitOfWorkImpl.class);

    }

    private <T> Factory<T> daoFactory(final DBI dbi, final Class<T> jdbiClass) {
        final DaoFactory<T> factory = new DaoFactory<>(dbi, jdbiClass);
        ServiceLocatorProvider.inject(factory);
        return factory;
    }

}
