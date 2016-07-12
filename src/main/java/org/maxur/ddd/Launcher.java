package org.maxur.ddd;


import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.yaml.snakeyaml.Yaml;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

import static io.baratine.web.Web.bean;
import static io.baratine.web.Web.go;
import static io.baratine.web.Web.include;
import static io.baratine.web.Web.property;
import static io.baratine.web.Web.service;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

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
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        bean(validatorFactory).to(ValidatorFactory.class);
        final UserConfig config = config(validatorFactory.getValidator());
        bean(config).to(UserConfig.class);
        property("server.port", String.valueOf(config.getRest().getPort()));
        include(SysConfig.class);
        service(TaskService.class);
        include(TaskResource.class);
        log.info(format("Start Service on http://%s", config.getRest()));
        go(args);
    }

    private static UserConfig config(final Validator validator) {
        final Yaml yaml = new Yaml();
        final UserConfig config;
        try (InputStream in = Files.newInputStream(Paths.get(CONFIG_YAML))) {
            config = yaml.loadAs(in, UserConfig.class);
            val constraintViolation = validator.validate(config);
            if (!constraintViolation.isEmpty()) {
                throw new IllegalArgumentException(
                    format(
                        "User configuration error:\n %s",
                        constraintViolation.stream()
                            .map(validationMessageFrom())
                            .collect(joining(";\n"))
                    )
                );
            }
        } catch (IOException e) {
            log.error("Config file '" + CONFIG_YAML +"' is not accessible");
            throw new IllegalArgumentException(e);
        }
        return config;
    }

    private static Function<ConstraintViolation<UserConfig>, String> validationMessageFrom() {
        return cv -> format(
            "Field '%s.%s' %s",
            cv.getLeafBean().getClass().getSimpleName(),
            cv.getPropertyPath(),
            cv.getMessage()
        );
    }


}