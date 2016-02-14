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

package org.maxur.ddd.dao;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.maxur.ddd.config.ServiceLocatorProvider;
import org.skife.jdbi.v2.DBI;

import javax.inject.Inject;

/**
 * The type Dao factory.
 *
 * @param <T> the type parameter
 * @author myunusov
 * @version 1.0
 * @since <pre>02.02.2016</pre>
 */
public class DaoFactory<T> implements Factory<T> {

    private final DBI dbi;

    private final Class<T> jdbiClass;

    @Inject
    private ServiceLocator locator;

    /**
     * Instantiates a new Dao factory.
     *
     * @param dbi       the dbi
     * @param jdbiClass the jdbi class
     */
    private DaoFactory(DBI dbi, Class<T> jdbiClass) {
        this.dbi = dbi;
        this.jdbiClass = jdbiClass;
    }

    /**
     * Dao factory factory.
     *
     * @param <T>       the jdbi class type parameter
     * @param dbi       the dbi
     * @param jdbiClass the jdbi class
     * @return the factory
     */
    public static <T> Factory<T> daoFactory(final DBI dbi, final Class<T> jdbiClass) {
        final DaoFactory<T> factory = new DaoFactory<>(dbi, jdbiClass);
        ServiceLocatorProvider.inject(factory);
        return factory;
    }

    @Override
    public T provide() {
        final T result = dbi.onDemand(jdbiClass);
        if (result != null) {
            locator.inject(result);
        }
        return result;
    }

    @Override
    public void dispose(T instance) {
        // NOOP
    }
}