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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * The type Config.
 *
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

    /**
     * Gets data source factory.
     *
     * @return the data source factory
     */
    DataSourceFactory getDataSourceFactory() {
        return database;
    }

    /**
     * Gets security.
     *
     * @return the security
     */
    Security getSecurity() {
        return security;
    }

    /**
     * Gets scripts.
     *
     * @return the scripts
     */
    public List<String> getScripts() {
        return scripts;
    }

    /**
     * The type Security.
     */
    public static class Security {

        @NotNull
        private List<String> tokens = newArrayList();

        /**
         * Sets tokens.
         *
         * @param tokens the tokens
         */
        public void setTokens(List<String> tokens) {
            this.tokens = tokens;
        }

        /**
         * Gets tokens.
         *
         * @return the tokens
         */
        public List<String> getTokens() {
            return tokens;
        }
    }
}
