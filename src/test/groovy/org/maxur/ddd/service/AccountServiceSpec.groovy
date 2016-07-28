package org.maxur.ddd.service

import org.maxur.ddd.dao.UserDao
import spock.lang.Specification
/**
 * @author myunusov
 * @version 1.0
 * @since <pre>06.01.2016</pre>
 */
class AccountServiceSpec extends Specification {

    UserDao userDao


    AccountService sut

    void setup() {
        userDao = Mock(UserDao)

        sut = new AccountService(userDao)
    }


    def "should be return null current user without authentication"() {
        when: "Try get current user"
        def user = sut.findAll()
        then: "System returns business error"
        assert user == null
    }

}