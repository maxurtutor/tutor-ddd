/*
 * Copyright (c) 2016 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.maxur.ddd.config;

import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
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

import static org.maxur.ddd.dao.DaoFactory.daoFactory;

/**
 * Implementation of injection binder with convenience methods for
 * binding definitions.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>02.02.2016</pre>
 */
public final class Binder extends AbstractBinder {

    private final Environment env;

    private final DBI dbi;

    private Binder(Config cfg, Environment env) {
        this.env = env;
        this.dbi = new DBIFactory().build(env, cfg.getDataSourceFactory(), "db");
    }

    /**
     * Binder abstract binder.
     *
     * @param cfg the cfg
     * @param env the env
     * @return the abstract binder
     */
    public static AbstractBinder binder(Config cfg, Environment env) {
        ServiceLocatorFactory locatorFactory = ServiceLocatorFactory.getInstance();
        ServiceLocator locator = locatorFactory.create("ServiceLocator");
        ServiceLocatorProvider.makeSingleton(locator);
        env.getApplicationContext().getAttributes().setAttribute(ServletProperties.SERVICE_LOCATOR, locator);
        Binder binder = new Binder(cfg, env);
        ServiceLocatorUtilities.bind(locator, binder);
        return binder;
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
}