package org.maxur.ddd.config;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.skife.jdbi.v2.DBI;

import javax.inject.Inject;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>02.02.2016</pre>
 */
public class DaoFactory<T> implements Factory<T> {

    private final DBI dbi;

    private final Class<T> jdbiClass;

    @Inject
    private ServiceLocator locator;

    public DaoFactory(DBI dbi, Class<T> jdbiClass) {
        this.dbi = dbi;
        this.jdbiClass = jdbiClass;
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

    }
}