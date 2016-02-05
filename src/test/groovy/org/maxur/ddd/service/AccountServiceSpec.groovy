package org.maxur.ddd.service

import org.maxur.ddd.dao.UserDao
import org.maxur.ddd.domain.User
import org.skife.jdbi.v2.DBI
import org.slf4j.Logger
import spock.lang.Specification

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>06.01.2016</pre>
 */
class AccountServiceSpec extends Specification {

    AccountService sut

    DBI dbi

    User user

    UserDao userDao

    Logger logger


    void setup() {
        dbi = Mock(DBI)
        userDao = Mock(UserDao)
        logger = Mock(Logger)
        sut = new AccountService(userDao)
        sut.logger = logger
        user = new User("id", "name", "user@mail.org")
    }

    def "should be return null current user without authentication"() {
        when: "Try get current user"
        def user = sut.currentUser
        then: "System returns business error"
        assert user == null
    }

}