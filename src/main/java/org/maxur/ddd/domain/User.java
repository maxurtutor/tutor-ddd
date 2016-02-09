package org.maxur.ddd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
@SuppressWarnings("unused")
@ApiModel(value = "User Details")
public class User {

    @JsonProperty
    @ApiModelProperty(value = "The User's identifier.", required = true)
    private String id;

    @JsonProperty
    @ApiModelProperty(value = "The User's email.", required = true)
    private String email;

    @JsonProperty
    @ApiModelProperty(value = "The User's password.", required = false)
    private String password;

    @JsonProperty
    private Collection<String> roles = new HashSet<>();

    public User() {
    }

    public User(String id, String email, String password) {
        this.id = id;
        this.password = password;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }

    public void addRole(String role) {
        roles.add(role);
    }

    public boolean is(String role) {
        return roles.contains(role);
    }
}
