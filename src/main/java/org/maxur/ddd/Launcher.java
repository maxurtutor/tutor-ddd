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

import java.net.URL;

import static org.maxur.ddd.RestApp.application;

/**
 * ## Launcher for the T-DDD application.
 *
 * ### T-DDD Application is One-Jar Standalone Rest Application.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
public final class Launcher {

    private static final String CONFIG_FILE = "dropwizard.yml";

    private Launcher() {
    }

    /**
     * # Command line entry point. This method kicks off the building of a application  object and executes it.
     * <p>
     * @param args - arguments of command. (Ignored)
     */
    public static void main(String[] args) throws Exception {
        URL resource = Launcher.class.getClassLoader().getResource(CONFIG_FILE);
        if (resource == null) {
            return;
        }
        application().run("server", resource.toString());
    }

}
