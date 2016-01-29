package org.maxur.ddd.service

import org.maxur.ddd.domain.Team
import org.maxur.ddd.domain.User
import org.skife.jdbi.v2.DBI
import org.slf4j.Logger
import spock.lang.Specification

import javax.mail.MessagingException

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>06.01.2016</pre>
 */
class AccountServiceTest extends Specification {

    AccountService sut

    MailService mailService

    User user

    Team team

    TeamDao teamDao

    UserDao userDao

    AccountDao accountDao

    Logger logger


    void setup() {
        mailService = Mock(MailService)
        teamDao = Mock(TeamDao)
        userDao = Mock(UserDao)
        accountDao = Mock(AccountDao)
        logger = Mock(Logger)
        sut = new AccountService(userDao, teamDao, accountDao, mailService)
        sut.logger = logger
        user = new User("id", "name", "firstName", "lastName", "user@mail.org", "teamId")
        team = new Team()
        team.id = "teamId"
        team.name = "teamName"
        team.maxCapacity = 1
    }

    def "User cannot be created without id"() {
        given: "User without id"
            user.id = null
        when: "Try create user"
            sut.create(user)
        then: "System returns business error"
            thrown(BusinessException.class)
    }

    def "User cannot be created without name"() {
        given: "User without Name"
        user.name = null
        when: "Try create user"
        sut.create(user)
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without First Name"() {
        given: "User without First Name"
        user.firstName = null
        when: "Try create user"
        sut.create(user)
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without Last Name"() {
        given: "User without Last Name"
        user.lastName = null
        when: "Try create user"
        sut.create(user)
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without email"() {
        given: "User without email"
        user.email = null
        when: "Try create user"
        sut.create(user)
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created with invalid email"() {
        given: "User with invalid email"
        user.email = "invalid"
        when: "Try create user"
        sut.create(user)
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created with invalid team id"() {
        given: "User with invalid team id"
        when: "Try create user"
        sut.create(user)
        then:
        1 * teamDao.findById("teamId") >> null
        and: "System returns business error"
        thrown(NotFoundException.class)
    }

    def "User cannot be created if team is overloaded"() {
        when: "Try create user"
        sut.create(user)
        then:
        1 * teamDao.findById("teamId") >> team
        1 * userDao.findCountByTeam("teamId") >> 1
        and: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created if user id is not unique"() {
        when: "Try create user"
        sut.create(user)
        then:
        1 * teamDao.findById("teamId") >> team
        1 * userDao.findCountByTeam("teamId") >> 2
        1 * accountDao.save(user, team) >> {
            throw new RuntimeException()
        }
        and: "System returns business error"
        thrown(BusinessException.class)
    }

    def "System write error to log if Mail Service inaccessible"() {
        when: "Try create user"
        def created = sut.create(user)
        then:
        1 * teamDao.findById("teamId") >> team
        1 * userDao.findCountByTeam("teamId") >> 0
        and: "User Created"
        created != null
        and: "User Saved"
        1 * accountDao.save(user, team)
        and: "Mail Client throws Exception "
        1 * mailService.send(_) >> {
             mail -> throw new MessagingException("message")
        }
        and: "Log error"
        1 * logger.error(_, _ as MessagingException)
    }

    def "User can be created"() {
        when: "Try create user"
        def created = sut.create(user)
        then:
        1 * teamDao.findById("teamId") >> team
        1 * userDao.findCountByTeam("teamId") >> 0
        and: "User Created"
        created != null
        and: "User Saved"
        1 * accountDao.save(user, team)
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can be updated"() {
        when: "Try update user"
        def created = sut.update(user)
        then:
        1 * teamDao.findById("teamId") >> team
        1 * userDao.findCountByTeam("teamId") >> 0
        and: "User Updated"
        created != null
        and: "User Saved"
        1 * accountDao.update(user, team)
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can be deleted"() {
        when: "Try delete user"
        sut.delete("id1")
        then:
        1 * teamDao.findById("teamId") >> team
        1 * userDao.findById("id1") >> user
        and: "User deleted"
        1 * accountDao.delete("id1", team)
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can change password"() {
        when: "Try change password"
        sut.changePassword("id1", "password")
        then:
        1 * userDao.findById("id1") >> user
        and: "Password changed"
        1 * userDao.changePassword("id1", '5f4dcc3b5aa765d61d8327deb882cf99')
        and: "System sends notification"
        1 * mailService.send(_)
    }

}
