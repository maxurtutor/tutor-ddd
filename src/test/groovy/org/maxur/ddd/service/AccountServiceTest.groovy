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

    static USER_ID = new Id<User>().asString()

    static TEAM_ID = new Id<Team>().asString()

    static OTHER_TEAM_ID = new Id<Team>().asString()

    AccountService sut

    MailService mailService

    UserBuilder baseUser = UserBuilder.buider(USER_ID, TEAM_ID)

    TeamBuilder baseTeam = TeamBuilder.buider(TEAM_ID)

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
        sut = new AccountService(userDao, teamDao, accountDao, mailService)
        sut.logger = logger
    }

    def "User cannot be created without id"() {
        given:
        def user = baseUser.but("id", null)
        when: "Try create user"
        sut.createUserBy(user.name, user.teamId, Person.person(user.firstName, user.lastName), EmailAddress.email(user.email))
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without name"() {
        given:
        def user = baseUser.but("name", null)
        when: "Try create user"
        sut.createUserBy(user.name, user.teamId, Person.person(user.firstName, user.lastName), EmailAddress.email(user.email))
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without First Name"() {
        given:
        def user = baseUser.but("firstName", null)
        when: "Try create user"
        sut.createUserBy(user.name, user.teamId, Person.person(user.firstName, user.lastName), EmailAddress.email(user.email))
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without Last Name"() {
        given:
        def user = baseUser.but("lastName", null)
        when: "Try create user"
        sut.createUserBy(user.name, user.teamId, Person.person(user.firstName, user.lastName), EmailAddress.email(user.email))
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without email"() {
        given:
        def user = baseUser.but("email", null)
        when: "Try create user"
        sut.createUserBy(user.name, user.teamId, Person.person(user.firstName, user.lastName), EmailAddress.email(user.email))
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created with invalid email"() {
        given:
        def user = baseUser.but("email", "invalid")
        when: "Try create user"
        sut.createUserBy(user.name, user.teamId, Person.person(user.firstName, user.lastName), EmailAddress.email(user.email))
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created with invalid team id"() {
        given:
        def user = baseUser
        when: "Try create user"
        sut.createUserBy(user.name, user.teamId, Person.person(user.firstName, user.lastName), EmailAddress.email(user.email))
        then:
        1 * teamDao.findById(TEAM_ID) >> null
        and: "System returns business error"
        thrown(NotFoundException.class)
    }

    def "User cannot be created if team is overloaded"() {
        given:
        def user = baseUser
        when: "Try create user"
        sut.createUserBy(user.name, user.teamId, Person.person(user.firstName, user.lastName), EmailAddress.email(user.email))
        then:
        1 * teamDao.findById(TEAM_ID) >> baseTeam.make()
        1 * locator.getService(UserDao) >> userDao
        1 * userDao.findCountByTeam(TEAM_ID) >> 1
        and: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created if user id is not unique"() {
        given:
        def user = baseUser
        when: "Try create user"
        sut.createUserBy(user.name, user.teamId, Person.person(user.firstName, user.lastName), EmailAddress.email(user.email))
        then:
        1 * teamDao.findById(TEAM_ID) >> baseTeam.make()
        1 * locator.getService(UserDao) >> userDao
        1 * userDao.findCountByTeam(TEAM_ID) >> 2
        1 * accountDao.save(_, _) >> {
            throw new RuntimeException()
        }
        and: "System returns business error"
        thrown(BusinessException.class)
    }

    def "System write error to log if Mail Service inaccessible"() {
        given:
        def user = baseUser
        when: "Try create user"
        def created = sut.createUserBy(user.name, user.teamId, Person.person(user.firstName, user.lastName), EmailAddress.email(user.email))
        then:
        1 * teamDao.findById(TEAM_ID) >> baseTeam.make()
        1 * locator.getService(UserDao) >> userDao
        1 * userDao.findCountByTeam(TEAM_ID) >> 0
        and: "User Created"
        created != null
        and: "User Saved"
        1 * accountDao.save(_, _)
        and: "Mail Client throws Exception "
        1 * mailService.send(_) >> {
            mail -> throw new MessagingException("message")
        }
        and: "Log error"
        1 * logger.error(_)
    }

    def "User can be created"() {
        given:
        def user = baseUser
        when: "Try create user"
        def created = sut.createUserBy(user.name, user.teamId, Person.person(user.firstName, user.lastName), EmailAddress.email(user.email))
        then:
        1 * teamDao.findById(TEAM_ID) >> baseTeam.make()
        1 * locator.getService(UserDao) >> userDao
        1 * userDao.findCountByTeam(TEAM_ID) >> 0
        and: "User Created"
        created != null
        and: "User Saved"
        1 * accountDao.save(*_) >> {
            arguments -> User user1 = arguments[0]
            user1.name == baseUser.name
        }
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can be updated"() {
        when: "Try update user"
        def created = sut.update(USER_ID, baseUser.make())
        then:
        1 * userDao.findById(USER_ID) >> baseUser.make()
        1 * teamDao.findById(TEAM_ID) >> baseTeam.make()
        and: "User Updated"
        created != null
        and: "User Saved"
        1 * accountDao.update(baseUser.make(), baseTeam.make())
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can be removed to other team"() {
        when: "Try update user"
        def created = sut.update(USER_ID, baseUser.but("teamId", OTHER_TEAM_ID).make())
        then:
        1 * userDao.findById(USER_ID) >> baseUser.make()
        1 * teamDao.findById(OTHER_TEAM_ID) >> baseTeam.but("id", OTHER_TEAM_ID).but("name", "other_team_name").make()
        1 * locator.getService(UserDao) >> userDao
        1 * userDao.findCountByTeam(OTHER_TEAM_ID) >> 0
        and: "User Updated"
        created != null
        and: "User Saved"
        1 * accountDao.update(baseUser.but("teamId", OTHER_TEAM_ID).make(),
                baseTeam.but("id", OTHER_TEAM_ID).but("name", "other_team_name").make())
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can be deleted"() {
        when: "Try delete user"
        sut.deleteUserBy(USER_ID)
        then:
        1 * teamDao.findById(TEAM_ID) >> baseTeam.make()
        1 * userDao.findById(USER_ID) >> baseUser.make()
        and: "User deleted"
        1 * accountDao.delete(baseUser.make(), baseTeam.make())
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can change password"() {
        when: "Try change password"
        sut.changeUserPassword(USER_ID, "password")
        then:
        1 * userDao.findById(USER_ID) >> baseUser.make()
        and: "Password changed"
        1 * userDao.changePassword(USER_ID, '5f4dcc3b5aa765d61d8327deb882cf99')
        and: "System sends notification"
        1 * mailService.send(_)
    }

    static class UserBuilder extends ObjectBuilder<UserBuilder, User> {

        static UserBuilder buider(String id, String teamId) {
            def builder = new UserBuilder()
            builder.set("id", id);
            builder.set("name", "name");
            builder.set("firstName", "firstName");
            builder.set("lastName", "lastName");
            builder.set("email", "user@mail.org");
            builder.set("teamId", teamId);
            return builder;
        }

        @Override
        User make() {
            User.oldUser(this.id, this.name, this.teamId, Person.person(this.firstName, this.lastName), EmailAddress.email(this.email))
        }
    }

    static class TeamBuilder extends ObjectBuilder<TeamBuilder, Team> {

        static TeamBuilder buider(String id) {
            def builder = new TeamBuilder()
            builder.set("id", id);
            builder.set("name", "teamName");
            builder.set("maxCapacity", 1);
            return builder;
        }

        @Override
        Team make() {
            return new Team(this.id, this.name, this.maxCapacity)
        }
    }

}
