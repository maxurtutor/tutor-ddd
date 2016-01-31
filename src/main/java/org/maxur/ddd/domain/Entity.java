package org.maxur.ddd.domain;

import java.util.Objects;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>30.01.2016</pre>
 */
public class Entity<T extends Entity> {

    private final Id<T> id;

    public Entity() {
        this.id = new Id<>();
    }

    public Entity(Id<T> id) {
        this.id = id;
    }

    public Id<T> getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity user = (Entity) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
