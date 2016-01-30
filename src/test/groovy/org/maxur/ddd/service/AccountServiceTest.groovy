package org.maxur.ddd.service

import org.glassfish.hk2.api.ServiceLocator
import org.maxur.ddd.ObjectBuilder
import org.maxur.ddd.domain.*
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

    UserBuilder baseUser = UserBuilder.buider()

    TeamBuilder baseTeam = TeamBuilder.buider()

    TeamDao teamDao

    UserDao userDao

    AccountDao accountDao

    Logger logger

    ServiceLocator locator

    void setup() {
        locator = Mock(ServiceLocator)
        new ServiceLocatorProvider(locator);
        mailService = Mock(MailService)
        teamDao = Mock(TeamDao)
        userDao = Mock(UserDao)
        accountDao = Mock(AccountDao)
        logger = Mock(Logger)
        sut = new AccountService(userDao, teamDao, accountDao, mailService, ServiceLocatorProvider.instance)
        sut.logger = logger
    }

    def "User cannot be created without id"() {
        when: "Try create user"
        sut.create(baseUser.but("id", null).make())
        then: "System returns business error"
            thrown(BusinessException.class)
    }

    def "User cannot be created without name"() {
        when: "Try create user"
        sut.create(baseUser.but("name", null).make())
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without First Name"() {
        when: "Try create user"
        sut.create(baseUser.but("firstName", null).make())
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without Last Name"() {
        when: "Try create user"
        sut.create(baseUser.but("lastName", null).make())
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without email"() {
        when: "Try create user"
        sut.create(baseUser.but("email", null).make())
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created with invalid email"() {
        when: "Try create user"
        sut.create(baseUser.but("email", "invalid").make())
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created with invalid team id"() {
        when: "Try create user"
        sut.create(baseUser.make())
        then:
        1 * teamDao.findById("teamId") >> null
        and: "System returns business error"
        thrown(NotFoundException.class)
    }

    def "User cannot be created if team is overloaded"() {
        when: "Try create user"
        sut.create(baseUser.make())
        then:
        1 * teamDao.findById("teamId") >> baseTeam.make()
        1 * locator.getService(UserDao) >> userDao
        1 * userDao.findCountByTeam("teamId") >> 1
        and: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created if user id is not unique"() {
        when: "Try create user"
        sut.create(baseUser.make())
        then:
        1 * teamDao.findById("teamId") >> baseTeam.make()
        1 * locator.getService(UserDao) >> userDao
        1 * userDao.findCountByTeam("teamId") >> 2
        1 * accountDao.save(baseUser.make(), baseTeam.make()) >> {
            throw new RuntimeException()
        }
        and: "System returns business error"
        thrown(BusinessException.class)
    }

    def "System write error to log if Mail Service inaccessible"() {
        when: "Try create user"
        def created = sut.create(baseUser.make())
        then:
        1 * teamDao.findById("teamId") >> baseTeam.make()
        1 * locator.getService(UserDao) >> userDao
        1 * userDao.findCountByTeam("teamId") >> 0
        and: "User Created"
        created != null
        and: "User Saved"
        1 * accountDao.save(baseUser.make(), baseTeam.make())
        and: "Mail Client throws Exception "
        1 * mailService.send(_) >> {
             mail -> throw new MessagingException("message")
        }
        and: "Log error"
        1 * logger.error(_)
    }

    def "User can be created"() {
        when: "Try create user"
        def created = sut.create(baseUser.make())
        then:
        1 * teamDao.findById("teamId") >> baseTeam.make()
        1 * locator.getService(UserDao) >> userDao
        1 * userDao.findCountByTeam("teamId") >> 0
        and: "User Created"
        created != null
        and: "User Saved"
        1 * accountDao.save(baseUser.make(), baseTeam.make())
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can be updated"() {
        when: "Try update user"
        def created = sut.update(baseUser.make())
        then:
        1 * userDao.findById("id") >> baseUser.make()
        1 * teamDao.findById("teamId") >> baseTeam.make()
        and: "User Updated"
        created != null
        and: "User Saved"
        1 * accountDao.update(baseUser.make(), baseTeam.make())
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can be removed to other team"() {
        when: "Try update user"
        def created = sut.update(baseUser.but("teamId", "other_teamId").make())
        then:
        1 * userDao.findById("id") >> baseUser.make()
        1 * teamDao.findById("other_teamId") >> baseTeam.but("id", "other_teamId").but("name", "other_team_name").make()
        1 * locator.getService(UserDao) >> userDao
        1 * userDao.findCountByTeam("other_teamId") >> 0
        and: "User Updated"
        created != null
        and: "User Saved"
        1 * accountDao.update(baseUser.but("teamId", "other_teamId").make(),
                baseTeam.but("id", "other_teamId").but("name", "other_team_name").make())
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can be deleted"() {
        when: "Try delete user"
        sut.delete("id")
        then:
        1 * teamDao.findById("teamId") >> baseTeam.make()
        1 * userDao.findById("id") >> baseUser.make()
        and: "User deleted"
        1 * accountDao.delete(baseUser.make(), baseTeam.make())
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can change password"() {
        when: "Try change password"
        sut.changePassword("id", "password")
        then:
        1 * userDao.findById("id") >> baseUser.make()
        and: "Password changed"
        1 * userDao.changePassword("id", '5f4dcc3b5aa765d61d8327deb882cf99')
        and: "System sends notification"
        1 * mailService.send(_)
    }

    static class UserBuilder extends ObjectBuilder<UserBuilder, User> {

        static UserBuilder buider () {
            def builder = new UserBuilder()
            builder.set("id", "id");
            builder.set("name", "name");
            builder.set("firstName", "firstName");
            builder.set("lastName", "lastName");
            builder.set("email", "user@mail.org");
            builder.set("teamId", "teamId");
            return builder;
        }

        @Override
        User make() {
            User.make(this.id, this.name, this.firstName, this.lastName, this.email, this.teamId)
        }
    }

    static class TeamBuilder extends ObjectBuilder<TeamBuilder, Team> {

        static TeamBuilder buider () {
            def builder = new TeamBuilder()
            builder.set("id", "teamId");
            builder.set("name", "teamName");
            builder.set("maxCapacity", 1);
            return builder;
        }

        @Override
        Team make() {
            def team = new Team()
            team.id = this.id
            team.name = this.name
            team.maxCapacity = this.maxCapacity
            return team
        }
    }

}
