package org.maxur.ddd.service;

import org.maxur.ddd.dao.UserDao;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.service.components.LoggerFactory;
import org.maxur.ddd.service.components.NotFoundException;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Collection;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class AccountService {

    private final Logger logger;

    private final UserDao dao;

    @Inject
    public AccountService(UserDao dao, LoggerFactory loggerFactory) {
        this.dao = dao;
        this.logger = loggerFactory.loggerFor(AccountService.class);
    }

    public Collection<User> findAll() {
        return dao.findAll();
    }

    public User findById(String id) throws NotFoundException {
        final User user = dao.findById(id);
        if (user == null) {
            throw new NotFoundException("User", id);
        }
        return user;
    }


}
