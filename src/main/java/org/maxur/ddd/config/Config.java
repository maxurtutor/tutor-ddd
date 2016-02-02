package org.maxur.ddd.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>02.02.2016</pre>
 */
public class Config extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();

    DataSourceFactory getDataSourceFactory() {
        return database;
    }
}
