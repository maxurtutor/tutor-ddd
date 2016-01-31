package org.maxur.ddd.service;

import org.maxur.ddd.domain.Entity;
import org.maxur.ddd.domain.Id;

import java.util.HashMap;
import java.util.Map;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>31.01.2016</pre>
 */
public class IdentificationMap {

    private Map<Id, Entity> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Entity> T get(Id<T> id) {
        return (T) map.get(id);
    }

    public <T extends Entity> void put(T entity) {
        map.put(entity.getId(), entity);
    }

    public void clear() {
        map.clear();
    }
}
