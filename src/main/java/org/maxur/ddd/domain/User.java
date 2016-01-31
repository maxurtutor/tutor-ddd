package org.maxur.ddd.domain;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.maxur.ddd.domain.NotificationService.Notification.CHANGE_PASSWORD;
import static org.maxur.ddd.domain.NotificationService.Notification.USER_FIRE;
import static org.maxur.ddd.domain.NotificationService.Notification.WELCOME;
import static org.maxur.ddd.domain.ServiceLocatorProvider.service;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
public class User extends Entity<User> {

    private String name;

    private Person person;

    private Password password;

    private Team team;

    private User(Id<User> id, String name, Person person, Password password) {
        super(id);
        this.name = name;
        this.password = password;
        this.person = person;
    }

    private User(String name, Person person, Password password) {
        super();
        this.name = name;
        this.password = password;
        this.person = person;
    }

    public static User newUser(String name, Person person) throws BusinessException {
        final Password password = Password.empty();
        return new User(checkName(name), person, password);
    }

    public static User restore(Snapshot snapshot) throws BusinessException {
        final Person person = Person.person(snapshot.firstName, snapshot.lastName, EmailAddress.email(snapshot.email));
        final Password password = Password.restore(snapshot.password);
        final User user =
                new User(Id.id(snapshot.getId()), checkName(snapshot.getName()), person, password);
        user.setTeam(Team.restore(snapshot.getTeam()));
        return user;
    }

    public Snapshot getSnapshot() {
        final Snapshot snapshot = new Snapshot();
        snapshot.setId(getId().asString());
        snapshot.setName(this.name);
        snapshot.setFirstName(this.person.getFirstName());
        snapshot.setLastName(this.person.getLastName());
        snapshot.setEmail(this.person.getEmailAddress().asString());
        snapshot.setTeam(this.team.getSnapshot());
        snapshot.setPassword(this.password.getPassword());
        return snapshot;
    }

    private static String checkName(String name) throws BusinessException {
        if (isNullOrEmpty(name)) {
            throw new BusinessException("User Name must not be empty");
        }
        return name;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return person.getFirstName();
    }

    public String getLastName() {
        return person.getLastName();
    }

    public String getEmail() {
        return person.getEmailAddress().asString();
    }

    public Id<Team> getTeamId() {
        return team != null ? team.getId() : null;
    }

    public String getTeamName() {
        return getTeam().getName();
    }

    public Team getTeam() {
        return team;
    }

    private void setTeam(Team team) {
        this.team = team;
    }

    public String getPassword() {
        return password.getPassword();
    }

    public User moveTo(Team team) throws BusinessException {
        if (includedTo(team)) {
            return this;
        }
        team.checkTeamCapacity();
        setTeam(team);
        sendNotification(WELCOME, getTeamName());
        return this;
    }

    public void changePassword(String password) throws BusinessException {
        if (isNullOrEmpty(password)) {
            throw new BusinessException("User password must not be empty");
        }
        this.password = Password.encrypt(password);
        sendNotification(CHANGE_PASSWORD);
    }

    private void changePersonInfo(Person person) {
        this.person = person;
    }

    private boolean includedTo(Team team) {
        return java.util.Objects.equals(this.team, team);
    }

    public Person getPerson() {
        return person;
    }

    public void changeInfo(Person person, Team team) throws BusinessException {
        changePersonInfo(person);
        moveTo(team);
    }

    private void sendNotification(NotificationService.Notification notification, String... args) {
        service(NotificationService.class).send(person.getEmailAddress(), notification, args);
    }

    public void fire() {
        sendNotification(USER_FIRE);
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public static class Snapshot {
        private String id;
        private String name;
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private Team.Snapshot team;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getTeamId() {
            return team.getId();
        }

        public Team.Snapshot getTeam() {
            return team;
        }

        public void setTeam(Team.Snapshot team) {
            this.team = team;
        }
    }

}
