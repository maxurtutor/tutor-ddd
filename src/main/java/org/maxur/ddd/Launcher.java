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
public class Launcher {

    private static final String CONFIG_FILE = "dropwizard.yml";

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
