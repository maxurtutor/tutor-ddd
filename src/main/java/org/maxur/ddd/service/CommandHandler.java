package org.maxur.ddd.service;

import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.01.2016</pre>
 */
public class CommandHandler {

    private final ServiceLocator locator;

    @Inject
    public CommandHandler(ServiceLocator locator) {
        this.locator = locator;
    }

    public <T> T handle(Command<T> command) throws BusinessException {
        locator.inject(command);
        command.validate();
        command.execute();
        return command.result();
    }
}
