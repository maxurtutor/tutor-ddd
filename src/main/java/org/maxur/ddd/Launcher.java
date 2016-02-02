package org.maxur.ddd;

import java.net.URL;

import static org.maxur.ddd.TdddApp.application;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
public class Launcher {

    private static final String CONFIG_FILE = "tddd.yml";

    public static void main(String[] args) throws Exception {
        URL resource = Launcher.class.getClassLoader().getResource(CONFIG_FILE);
        if (resource == null) {
            return;
        }
        application().run("server", resource.toString());
    }

}
