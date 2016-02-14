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

import org.glassfish.hk2.api.ServiceLocator;
import org.jetbrains.annotations.Contract;

/**
 * The type Service locator provider.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>11/6/2015</pre>
 */
public class ServiceLocatorProvider {

    private static ServiceLocatorProvider instance;

    private final ServiceLocator locator;

    /**
     * Instantiates a new Service locator provider.
     *
     * @param locator the locator
     */
    private ServiceLocatorProvider(final ServiceLocator locator) {
        this.locator = locator;
    }

    @Contract("!null -> !null")
    static ServiceLocatorProvider makeSingleton(final ServiceLocator locator) {
        if (locator == null) {
            throw  new IllegalArgumentException("Service Locator must not be null");
        }
        if (instance != null) {
            return instance;
        }
        instance = new ServiceLocatorProvider(locator);
        return instance;
    }

    /**
     * Service t.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the t
     */
    public static <T> T service(Class<T> clazz) {
        return instance.locator.getService(clazz);
    }

    /**
     * Inject.
     *
     * @param bean the bean
     */
    public static void inject(Object bean) {
        if (bean == null) {
            return;
        }
        instance.locator.inject(bean);
    }

}