package org.maxur.ddd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
@SuppressWarnings("unused")
public class Team {

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private Integer maxCapacity;

    public Team() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
