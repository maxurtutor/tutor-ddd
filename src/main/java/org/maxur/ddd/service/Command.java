package org.maxur.ddd.service;

import org.maxur.ddd.domain.User;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.01.2016</pre>
 */
public abstract class Command<T> {

    public abstract T result();

    public abstract void validate() throws BusinessException;

    public abstract void execute() throws BusinessException;

    void rollback() {

    }
}
