package org.maxur.ddd.service

import org.maxur.ddd.dao.UserDao
import org.maxur.ddd.service.components.LoggerFactory
import org.slf4j.Logger
import spock.lang.Specification
/**
 * @author myunusov
 * @version 1.0
 * @since <pre>06.01.2016</pre>
 */
class AccountServiceSpec extends Specification {

    UserDao userDao

    Logger logger

    AccountService sut

    void setup() {
        userDao = Mock(UserDao)

        logger = Mock(Logger)

        def factory = new LoggerFactory() {
            @Override
            Logger loggerFor(Class<?> clazz) {
                return logger
            }
        }

        sut = new AccountService(userDao, factory)
    }


    def "should be return null current user without authentication"() {
        when: "Try get current user"
        def user = sut.findAll()
        then: "System returns business error"
        assert user == null
    }

}