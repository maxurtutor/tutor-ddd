package org.maxur.ddd.service.ioc;

import java.lang.annotation.Annotation;

/**
 * The interface Service locator.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/28/2016</pre>
 */
public interface ServiceLocator {

    /**
     * Bean t.
     *
     * @param <T>         the type parameter
     * @param aClass      the a class
     * @param annotations the annotations
     * @return the t
     */
    <T> T bean(Class<T> aClass, Annotation... annotations);
}
