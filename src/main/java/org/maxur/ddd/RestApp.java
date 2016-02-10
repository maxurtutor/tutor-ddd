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

package org.maxur.ddd;

import com.codahale.metrics.JmxReporter;
import io.dropwizard.Application;
import io.dropwizard.Bundle;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.UrlConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.maxur.ddd.config.Config;

import java.io.IOException;
import java.sql.SQLException;

import static org.maxur.ddd.config.Binder.binder;
import static org.maxur.ddd.config.Rest.initRest;
import static org.maxur.ddd.dao.DBUtils.runScripts;

/**
 * ## This class represents T-DDD application.
 *
 * ### T-DDD Application is One-Jar Standalone Rest Application.
 * It's uses Dropwizard as core rest framework.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/4/2016</pre>
 */
public class RestApp extends Application<Config> {

    private static final AssetsBundle ASSETS_BUNDLE = new AssetsBundle("/assets", "/", "index.html");
    private static final AssetsBundle SWAGGER_BUNDLE = new AssetsBundle("/swagger-ui", "/api-docs", "index.html");

    @Contract(" -> !null")
    static RestApp application() {
        return new RestApp();
    }

    @Override
    /**
     * Initializes the application bootstrap.
     *
     * @param bootstrap the application bootstrap
     */
    public void initialize(@NotNull final Bootstrap<Config> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new UrlConfigurationSourceProvider());
        bootstrap.addBundle(ASSETS_BUNDLE);
        bootstrap.addBundle(SWAGGER_BUNDLE);
    }

    @Override
    /**
     * When the application runs, this is called after the {@link Bundle}s are run.
     *
     * @param configuration the parsed {@link Configuration} object
     * @param environment   the application's {@link Environment}
     *
     */
    public void run(@NotNull final Config cfg, @NotNull final Environment env) {
        JmxReporter.forRegistry(env.metrics()).build().start();
        binder(cfg, env);
        initRest(cfg, env);
        initDatabase(cfg);
    }

    private void initDatabase(@NotNull final Config cfg)  {
        try {
            runScripts(cfg.getScripts());
        } catch (IOException | SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
