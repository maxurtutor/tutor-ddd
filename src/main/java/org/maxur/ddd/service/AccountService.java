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

package org.maxur.ddd.service;

import org.maxur.ddd.dao.UserDao;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.service.components.LoggerFactory;
import org.maxur.ddd.service.components.NotFoundException;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Collection;

/**
 * The type Account service.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class AccountService {

    private final Logger logger;

    private final UserDao dao;

    /**
     * Instantiates a new Account service.
     *
     * @param dao           the user dao
     * @param loggerFactory the logger factory
     */
    @Inject
    public AccountService(UserDao dao, LoggerFactory loggerFactory) {
        this.dao = dao;
        this.logger = loggerFactory.loggerFor(AccountService.class);
    }

    /**
     * Find all users.
     *
     * @return the users collection
     */
    public Collection<User> findAll() {
        return dao.findAll();
    }

    /**
     * Find by id user.
     *
     * @param id the user id
     * @return the user
     * @throws NotFoundException the not found exception
     */
    public User findById(String id) throws NotFoundException {
        final User user = dao.findById(id);
        if (user == null) {
            final NotFoundException exception = new NotFoundException("User", id);
            logger.info(exception.getMessage());
            throw exception;
        }
        return user;
    }


}
