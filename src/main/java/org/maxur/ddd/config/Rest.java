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

import com.google.common.collect.Lists;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.chained.ChainedAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jetbrains.annotations.NotNull;
import org.maxur.ddd.ui.components.BaseAuthenticator;
import org.maxur.ddd.ui.components.BaseAuthorizer;
import org.maxur.ddd.ui.components.OAuthAuthenticator;
import org.maxur.ddd.ui.components.UserPrincipal;
import org.reflections.Reflections;

import javax.ws.rs.Path;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;
import java.util.Set;

import static org.maxur.ddd.config.ServiceLocatorProvider.service;

/**
 * The type Rest.
 */
public final class Rest {

    private final static Reflections reflections = new Reflections("org.maxur.ddd");

    private Rest() {
    }

    /**
     * Init rest.
     *
     * @param cfg the cfg
     * @param env the env
     */
    public static void initRest(Config cfg, Environment env) {
        JerseyEnvironment jersey = env.jersey();

        jersey.register(new AuthDynamicFeature(new ChainedAuthFilter(makeAuthFilters(cfg))));
        jersey.register(RolesAllowedDynamicFeature.class);
        jersey.register(new AuthValueFactoryProvider.Binder<>(UserPrincipal.class));

        Set<Class<? extends ExceptionMapper>> exceptionMappers = reflections.getSubTypesOf(ExceptionMapper.class);
        exceptionMappers.forEach(jersey::register);

        Set<Class<?>> resources = reflections.getTypesAnnotatedWith(Path.class);
        resources.forEach(jersey::register);

    }

    @NotNull
    private static List<AuthFilter> makeAuthFilters(Config cfg) {
        AuthFilter basicCredentialAuthFilter = new BasicCredentialAuthFilter.Builder<UserPrincipal>()
            .setAuthenticator(service(BaseAuthenticator.class))
            .setAuthorizer(new BaseAuthorizer())
            .setRealm("Basic")
            .buildAuthFilter();

        AuthFilter oauthCredentialAuthFilter = new OAuthCredentialAuthFilter.Builder<UserPrincipal>()
            .setAuthenticator(new OAuthAuthenticator(cfg.getSecurity()))
            .setAuthorizer(new BaseAuthorizer())
            .setPrefix("Bearer")
            .buildAuthFilter();


        return Lists.newArrayList(basicCredentialAuthFilter, oauthCredentialAuthFilter);
    }
}