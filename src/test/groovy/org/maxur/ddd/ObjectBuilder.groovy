package org.maxur.ddd

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>30.01.2016</pre>
 */
public abstract class ObjectBuilder<S extends ObjectBuilder, T> {

    def properties = [:]

    abstract T make()

    def getProperty(String name) {
        properties[name]
    }

    void setProperty(String name, value) {
        properties[name] = value
    }

    void set(def fieldName, def fieldVal) {
        setProperty(fieldName, fieldVal)
    }

    S but(String fieldName, String value) {
        this.set(fieldName, value)
        return this
    }
}
