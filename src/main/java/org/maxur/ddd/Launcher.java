package org.maxur.ddd;


import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.baratine.web.Web.bean;
import static io.baratine.web.Web.go;
import static io.baratine.web.Web.include;
import static io.baratine.web.Web.property;
import static io.baratine.web.Web.service;
import static java.lang.String.format;

/**
 * Application Launcher.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>10.07.2016</pre>
 */
@Slf4j
public final class Launcher {

    private static final String CONFIG_YAML = "./conf/config.yaml";

    private Launcher() {
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        rest(args);
    }

    private static void rest(String[] args) {
        final UserConfig config = config();
        log.info(format("Start Service on http://%s", config.getRest()));
        bean(config).to(UserConfig.class);
        property("server.port", String.valueOf(config.getRest().getPort()));
        include(SysConfig.class);
        service(TaskService.class);
        include(TaskResource.class);
        go(args);
    }

    private static UserConfig config() {
        final Yaml yaml = new Yaml();
        final UserConfig config;
        try (InputStream in = Files.newInputStream(Paths.get(CONFIG_YAML))) {
            config = yaml.loadAs(in, UserConfig.class);
        } catch (IOException e) {
            log.error("Config file '" + CONFIG_YAML +"' is not accessible");
            throw new IllegalArgumentException(e);
        }
        return config;
    }


}