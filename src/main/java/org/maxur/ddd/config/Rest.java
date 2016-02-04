package org.maxur.ddd.config;

import io.dropwizard.jersey.setup.JerseyEnvironment;
import org.reflections.Reflections;

import javax.ws.rs.Path;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Set;

public final class Rest {

    private final static Reflections reflections = new Reflections("org.maxur.ddd");

    private Rest() {
    }

    public static void initRest(JerseyEnvironment jersey) {

        Set<Class<? extends ExceptionMapper>> exceptionMappers = reflections.getSubTypesOf(ExceptionMapper.class);
        exceptionMappers.forEach(jersey::register);

        Set<Class<?>> resources = reflections.getTypesAnnotatedWith(Path.class);
        resources.forEach(jersey::register);

    }
}