package org.maxur.ddd.service

import org.glassfish.hk2.api.ServiceLocator
import org.maxur.ddd.utils.ObjectBuilder
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

    TeamRepository teamRepository

    UserRepository userRepository

    Logger logger

    ServiceLocator locator

    UnitOfWork unitOfWork

    UnitOfWorkImpl unitOfWorkImpl

    NotificationService notificationService

    void setup() {
        unitOfWorkImpl = Mock(UnitOfWorkImpl)
        unitOfWork = new UnitOfWork(unitOfWorkImpl)
        logger = Mock(Logger)
        locator = Mock(ServiceLocator)
        new ServiceLocatorProvider(locator);
        mailService = Mock(MailService)
        notificationService = new NotificationServiceImpl(mailService)
        notificationService.logger = logger
        teamRepository = Mock(TeamRepository)
        userRepository = Mock(UserRepository)
        sut = new AccountService(userRepository, teamRepository)

    }

    def "User cannot be created without id"() {
        given:
        def user = baseUser.but("id", null)
        when: "Try create user"
        sut.createUserBy(user.name, user.person(), user.teamId())
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without name"() {
        given:
        def user = baseUser.but("name", null)
        when: "Try create user"
        sut.createUserBy(user.name, user.person(), user.teamId())
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without First Name"() {
        given:
        def user = baseUser.but("firstName", null)
        when: "Try create user"
        sut.createUserBy(user.name, user.person(), user.teamId())
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without Last Name"() {
        given:
        def user = baseUser.but("lastName", null)
        when: "Try create user"
        sut.createUserBy(user.name, user.person(), user.teamId())
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created without email"() {
        given:
        def user = baseUser.but("email", null)
        when: "Try create user"
        sut.createUserBy(user.name, user.person(), user.teamId())
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created with invalid email"() {
        given:
        def user = baseUser.but("email", "invalid")
        when: "Try create user"
        sut.createUserBy(user.name, user.person(), user.teamId())
        then: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created with invalid team id"() {
        given:
        def user = baseUser
        when: "Try create user"
        sut.createUserBy(user.name, user.person(), user.teamId())
        then:
        1 * teamRepository.findById(TEAM_ID) >> null
        and: "System returns business error"
        thrown(NotFoundException.class)
    }

    def "User cannot be created if team is overloaded"() {
        given:
        def user = baseUser
        when: "Try create user"
        sut.createUserBy(user.name, user.person(), user.teamId())
        then:
        1 * teamRepository.findById(TEAM_ID) >> baseTeam.make()
        1 * locator.getService(UserRepository) >> userRepository
        1 * userRepository.findCountByTeam(TEAM_ID) >> 1
        and: "System returns business error"
        thrown(BusinessException.class)
    }

    def "User cannot be created if user id is not unique"() {
        given:
        def user = baseUser
        when: "Try create user"
        sut.createUserBy(user.name, user.person(), user.teamId())
        then:
        1 * teamRepository.findById(TEAM_ID) >> baseTeam.make()
        1 * locator.getService(UserRepository) >> userRepository
        1 * locator.getService(UnitOfWork) >> unitOfWork
        1 * locator.getService(NotificationService) >> notificationService
        1 * userRepository.findCountByTeam(TEAM_ID) >> 2
        1 * unitOfWorkImpl.commit(_) >> {
            arguments -> arguments[0].run()
        }
        1 * unitOfWorkImpl.insert(_, _) >> {
            throw new RuntimeException()
        }
        and: "System returns business error"
        thrown(BusinessException.class)
    }

    def "System write error to log if Mail Service inaccessible"() {
        given:
        def user = baseUser
        when: "Try create user"
        def created = sut.createUserBy(user.name, user.person(), user.teamId())
        then:
        1 * teamRepository.findById(TEAM_ID) >> baseTeam.make()
        1 * locator.getService(UserRepository) >> userRepository
        1 * locator.getService(UnitOfWork) >> unitOfWork
        1 * locator.getService(NotificationService) >> notificationService
        1 * userRepository.findCountByTeam(TEAM_ID) >> 0
        and: "User returned"
        assert created != null
        and: "User created"
        1 * unitOfWorkImpl.commit(_) >> {
            arguments -> arguments[0].run()
        }
        1 * unitOfWorkImpl.insert(User, _) >> {
            arguments -> List<User> users = arguments[1]
                assert users.size() == 1
                assert users[0] != null
                assert users[0].name == baseUser.name
        }
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
        def created = sut.createUserBy(user.name, user.person(), user.teamId())
        then:
        1 * teamRepository.findById(TEAM_ID) >> baseTeam.make()
        1 * locator.getService(UserRepository) >> userRepository
        1 * locator.getService(UnitOfWork) >> unitOfWork
        1 * locator.getService(NotificationService) >> notificationService
        1 * userRepository.findCountByTeam(TEAM_ID) >> 0
        and: "User returned"
        assert created != null
        and: "User created"
        1 * unitOfWorkImpl.commit(_) >> {
            arguments -> arguments[0].run()
        }
        1 * unitOfWorkImpl.insert(User, _) >> {
            arguments -> List<User> users = arguments[1]
                assert users.size() == 1
                assert users[0] != null
                assert users[0].name == baseUser.name
        }
        and: "Team updated"
        1 * unitOfWorkImpl.update(Team, _) >> {
            arguments -> List<Team> teams = arguments[1]
                assert teams.size() == 1
                assert teams[0] != null
                assert teams[0].name == baseTeam.name
        }
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can be updated"() {
        when: "Try update user"
        def result = sut.changeUserInfo(Id.id(USER_ID), baseUser.make().getPerson(), Id.id(TEAM_ID))
        then:
        1 * userRepository.findById(USER_ID) >> baseUser.make()
        1 * teamRepository.findById(TEAM_ID) >> baseTeam.make()
        1 * locator.getService(UnitOfWork) >> unitOfWork
        and: "User returned"
        assert result != null
        and: "User updated"
        1 * unitOfWorkImpl.commit(_) >> {
            arguments -> arguments[0].run()
        }
        1 * unitOfWorkImpl.update(User, _) >> {
            arguments -> List<User> users = arguments[1]
                assert users.size() == 1
                assert users.stream().anyMatch({user -> user.getName() == "name"})
        }
        and: "Team updated"
        1 * unitOfWorkImpl.update(Team, _) >> {
            arguments -> List<Team> teams = arguments[1]
                assert teams.size() == 1
                assert teams.stream().anyMatch({team -> team.getName() == "teamName"})
        }
        and: "System is not sending notification"
        0 * mailService.send(_)
    }

    def "User can be moved to other team"() {
        when: "Try update user"
        def result = sut.changeUserInfo(Id.id(USER_ID), baseUser.person(), Id.id(OTHER_TEAM_ID))
        then:
        1 * userRepository.findById(USER_ID) >> baseUser.make()
        1 * teamRepository.findById(TEAM_ID) >> baseTeam.make()
        1 * teamRepository.findById(OTHER_TEAM_ID) >> baseTeam.but("id", OTHER_TEAM_ID).but("name", "otherTeamName").make()
        1 * locator.getService(UserRepository) >> userRepository
        1 * locator.getService(UnitOfWork) >> unitOfWork
        1 * locator.getService(NotificationService) >> notificationService
        1 * userRepository.findCountByTeam(OTHER_TEAM_ID) >> 0
        and: "User updated"
        assert result != null
        and: "User updated"
        1 * unitOfWorkImpl.commit(_) >> {
            arguments -> arguments[0].run()
        }
        1 * unitOfWorkImpl.update(User, _) >> {
            arguments -> List<User> users = arguments[1]
                assert users.size() == 1
                assert users[0] != null
                assert users[0].name == baseUser.name
        }
        and: "New and old teams updated"
        1 * unitOfWorkImpl.update(Team, _) >> {
            arguments -> List<Team> teams = arguments[1]
                assert teams.size() == 2
                assert teams.stream().anyMatch({team -> team.getName() == "teamName"})
                assert teams.stream().anyMatch({team -> team.getName() == "otherTeamName"})
        }
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can be deleted"() {
        when: "Try delete user"
        sut.deleteUserBy(Id.id(USER_ID))
        then:
        1 * teamRepository.findById(TEAM_ID) >> baseTeam.make()
        1 * userRepository.findById(USER_ID) >> baseUser.make()
        1 * locator.getService(UnitOfWork) >> unitOfWork
        1 * locator.getService(NotificationService) >> notificationService
        and: "User deleted"
        1 * unitOfWorkImpl.commit(_) >> {
            arguments -> arguments[0].run()
        }
        1 * unitOfWorkImpl.delete(User, _) >> {
            arguments -> List<User> users = arguments[1]
                assert users.size() == 1
                assert users[0] != null
                assert users[0].name == baseUser.name
        }
        and: "Team updated"
        1 * unitOfWorkImpl.update(Team, _) >> {
            arguments -> List<Team> teams = arguments[1]
                assert teams.size() == 1
                assert teams[0] != null
                assert teams[0].name == baseTeam.name
        }
        and: "System sends notification"
        1 * mailService.send(_)
    }

    def "User can change password"() {
        when: "Try change password"
        sut.changeUserPassword(Id.id(USER_ID), "password")
        then:
        1 * userRepository.findById(USER_ID) >> baseUser.make()
        1 * locator.getService(UnitOfWork) >> unitOfWork
        1 * locator.getService(NotificationService) >> notificationService
        and: "Password changed"
        1 * unitOfWorkImpl.commit(_) >> {
            arguments -> arguments[0].run()
        }
        1 * unitOfWorkImpl.update(User, _) >> {
            arguments -> List<User> users = arguments[1]
                assert users.size() == 1
                assert users.stream().anyMatch({user -> user.getPassword() == '5f4dcc3b5aa765d61d8327deb882cf99'})
        }
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

        Person person() {
            Person.person(this.firstName, this.lastName, EmailAddress.email(this.email))
        }

        private Id teamId() {
            Id.id(this.teamId)
        }

        private Id userId() {
            Id.id(this.id)
        }

        @Override
        User make() {
            User.oldUser(userId(), this.name, teamId(), person())
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

        private Id teamId() {
            Id.id(this.id)
        }

        @Override
        Team make() {
            return new Team(teamId(), this.name, this.maxCapacity)
        }
    }

}
