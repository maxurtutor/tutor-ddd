/*
 * Copyright (c) 2016 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.maxur.ddd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Collection;
import java.util.HashSet;

/**
 * The type User.
 *
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

    /**
     * Instantiates a new User.
     */
    public User() {
    }

    /**
     * Instantiates a new User.
     *
     * @param id       the id
     * @param email    the email
     * @param password the password
     */
    public User(String id, String email, String password) {
        this.id = id;
        this.password = password;
        this.email = email;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets roles.
     *
     * @return the roles
     */
    public Collection<String> getRoles() {
        return roles;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets roles.
     *
     * @param roles the roles
     */
    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }

    /**
     * Add role.
     *
     * @param role the role
     */
    public void addRole(String role) {
        roles.add(role);
    }

    /**
     * Is boolean.
     *
     * @param role the role
     * @return the boolean
     */
    public boolean is(String role) {
        return roles.contains(role);
    }
}
