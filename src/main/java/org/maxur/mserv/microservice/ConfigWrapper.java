package org.maxur.mserv.microservice;

import lombok.experimental.Delegate;
import org.maxur.ddd.config.Config;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/29/2016</pre>
 */
public class ConfigWrapper implements MicroService {

    @Delegate
    private final MicroService microService;

    private final Class<Config> configClass;

    // TODO auto-registration
    private static final ConfigLoader[] loaders = {new YamlConfigLoader()};

    private ConfigWrapper(final MicroService microService, final Class<Config> configClass) {
        this.microService = microService;
        this.configClass = configClass;
    }

    public static ConfigWrapper wrap(final MicroService microService, final Class<Config> configClass) {
        return new ConfigWrapper(microService, configClass);
    }

    public MicroService from(final String fileName) {
        return microService;
    }
}
