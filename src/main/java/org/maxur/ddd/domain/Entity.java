package org.maxur.ddd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>11/19/2015</pre>
 */
public abstract class Entity {

    private final String id;

    public Entity() {
        this.id = UUID.randomUUID().toString();
    }

    public Entity(String id) {
        this.id = id;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
