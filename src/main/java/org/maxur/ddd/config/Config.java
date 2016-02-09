package org.maxur.ddd.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/4/2016</pre>
 */
public class Config extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    @JsonProperty
    private Security security = new Security();

    @Valid
    @NotNull
    @JsonProperty
    private List<String> scripts = new ArrayList<>();

    DataSourceFactory getDataSourceFactory() {
        return database;
    }

    Security getSecurity() {
        return security;
    }

    public List<String> getScripts() {
        return scripts;
    }

    public static class Security {

        @NotNull
        private List<String> tokens = newArrayList();

        public void setTokens(List<String> tokens) {
            this.tokens = tokens;
        }

        public List<String> getTokens() {
            return tokens;
        }
    }
}
