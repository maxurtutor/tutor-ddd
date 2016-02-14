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

package org.maxur.ddd.dao;

import org.maxur.ddd.domain.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The type User dao.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
public abstract class UserDao implements GetHandle { // NOSONAR

    /**
     * Find all Users.
     *
     * @return the set
     */
    public Set<User> findAll() {
        return getHandle().attach(UserQueryDao.class).findAll();
    }

    /**
     * Find by id user.
     *
     * @param id the id
     * @return the user
     */
    public User findById(String id) {
        final Iterator<User> iterator = getHandle().attach(UserQueryDao.class).findById(id).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    /**
     * Find by email user.
     *
     * @param email the email
     * @return the user
     */
    public User findByEmail(String email) {
        final Iterator<User> iterator = getHandle().attach(UserQueryDao.class).findByEmail(email).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }


    /**
     * The interface User query dao.
     */
    @RegisterMapper(UserQueryDao.Mapper.class)
    static interface UserQueryDao {
        /**
         * Find all set.
         *
         * @return the set
         */
        @SqlQuery(
                "SELECT\n" +
                        "  u.user_id,\n" +
                        "  email,\n" +
                        "  password,\n" +
                        "  role_id\n" +
                        "FROM t_user u LEFT JOIN t_user_role r ON u.user_id = r.user_id \n"
        )
        Set<User> findAll();

        /**
         * Find by id set.
         *
         * @param id the id
         * @return the set
         */
        @SqlQuery("SELECT\n" +
                "  u.user_id,\n" +
                "  email,\n" +
                "  password,\n" +
                "  role_id\n" +
                "FROM t_user u LEFT JOIN t_user_role r ON u.user_id = r.user_id\n" +
                "WHERE u.user_id = :id")
        Set<User> findById(@Bind("id") String id);

        /**
         * Find by email set.
         *
         * @param email the email
         * @return the set
         */
        @SqlQuery("SELECT\n" +
                "  u.user_id,\n" +
                "  email,\n" +
                "  password,\n" +
                "  role_id\n" +
                "FROM t_user u LEFT JOIN t_user_role r ON u.user_id = r.user_id\n" +
                "WHERE email = :email")
        Set<User> findByEmail(@Bind("email") String email);

        /**
         * The type Mapper.
         */
        class Mapper implements ResultSetMapper<User> {

            /**
             * The Users.
             */
            Map<String, User> users = new HashMap<>();

            @Override
            public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                final String email = r.getString("email");
                final String password = r.getString("password");
                final User user = users.computeIfAbsent(r.getString("user_id"), id -> new User(id, email, password));
                final String role = r.getString("role_id");
                if (role != null) {
                    user.addRole(role);
                }
                return user;
            }
        }
    }


}