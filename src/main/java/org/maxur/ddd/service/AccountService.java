package org.maxur.ddd.service;

import org.maxur.ddd.dao.UserDao;
import org.maxur.ddd.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class AccountService {

    private Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final UserDao dao;

    @Inject
    public AccountService(UserDao dao) {
        this.dao = dao;

    }

    public User getCurrentUser() {
        return null;
    }
}
