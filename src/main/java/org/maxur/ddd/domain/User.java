package org.maxur.ddd.domain;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
public class User extends Entity {

    private String name;

    private final Person person;

    private final EmailAddress emailAddress;

    private Password password;

    private String teamId;

    private String teamName;

    private User(String id, String name, Person person, Password password, EmailAddress emailAddress) {
        super(id);
        this.name = name;
        this.emailAddress = emailAddress;
        this.password = password;
        this.person = person;
    }

    public User(String name, Person person, Password password, EmailAddress emailAddress) {
        super();
        this.name = name;
        this.emailAddress = emailAddress;
        this.password = password;
        this.person = person;
    }

    public static User newUser(
            String name, String firstName, String lastName, String email, String teamId
    ) throws BusinessException {
        final Person person = Person.make(firstName, lastName);
        final EmailAddress emailAddress = EmailAddress.make(email);
        final Password password = Password.empty();
        final User user = new User(checkName(name), person, password, emailAddress);
        user.setTeamId(teamId);
        return user;
    }

    public static User oldUser(
            String id, String name, String firstName, String lastName, String email, String teamId
    ) throws BusinessException {
        final Person person = Person.make(firstName, lastName);
        final EmailAddress emailAddress = EmailAddress.make(email);
        final Password password = Password.empty();
        final User user = new User(checkId(id), checkName(name), person, password, emailAddress);
        user.setTeamId(teamId);
        return user;
    }

    public static User restore(Snapshot snapshot) throws BusinessException {
        final Person person = Person.make(snapshot.firstName, snapshot.lastName);
        final EmailAddress emailAddress = EmailAddress.make(snapshot.email);
        final Password password = Password.restore(snapshot.password);
        final User user =
                new User(checkId(snapshot.getId()), checkName(snapshot.getName()), person, password, emailAddress);
        user.setTeamId(snapshot.getTeamId());
        return user;
    }

    public Snapshot getSnapshot() {
        final Snapshot snapshot = new Snapshot();
        snapshot.setId(getId().asString());
        snapshot.setName(this.name);
        snapshot.setFirstName(this.person.getFirstName());
        snapshot.setLastName(this.person.getLastName());
        snapshot.setEmail(this.emailAddress.getEmail());
        snapshot.setTeamId(this.teamId);
        snapshot.setTeamName(this.teamName);
        snapshot.setPassword(this.password.getPassword());
        return snapshot;
    }

    private static String checkId(String id) throws BusinessException {
        if (isNullOrEmpty(id)) {
            throw new BusinessException("User Id must not be empty");
        }
        return id;
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
        return emailAddress.getEmail();
    }

    public String getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getPassword() {
        return password.getPassword();
    }

    private void setTeamId(String teamId) {
        this.teamId = teamId;
    }


    public User create(Team team) throws BusinessException {
        team.checkTeamCapacity();
        return this;
    }

    public User update(User old, Team team) throws BusinessException {
        if (!old.getTeamId().equals(teamId)) {
            team.checkTeamCapacity();
        }
        return this;
    }

    public void changePassword(String password) throws BusinessException {
        if (isNullOrEmpty(password)) {
            throw new BusinessException("User password must not be empty");
        }
        this.password = Password.encrypt(password);
    }

    @SuppressWarnings("WeakerAccess")
    public static class Snapshot {
        private String id;
        private String name;
        private String firstName;
        private String lastName;
        private String email;
        private String teamId;
        private String teamName;
        private String password;

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

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
