package org.maxur.ddd.domain;

import java.util.Optional;


/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
public class User extends Entity {

    private final FullName fullName;

    private String name;

    private EmailAddress email;

    private String groupId;

    private User(
            String name,
            FullName fullName,
            EmailAddress email,
            String groupId
    ) {
        super();
        this.name = name;
        this.fullName = fullName;
        this.email = email;
        this.groupId = groupId;
    }

    private User(
            String id,
            String name,
            FullName fullName,
            EmailAddress email,
            String groupId
    ) {
        super(id);
        this.name = name;
        this.fullName = fullName;
        this.email = email;
        this.groupId = groupId;
    }

    public static User newUser(
            String name,
            FullName fullName,
            EmailAddress email,
            String groupId
    ){
        return new User(name, fullName, email, groupId);
    }

    public Snapshot getSnapshot() {
        return new Snapshot(
                getId(),
                this.name,
                this.fullName.getFirstName(),
                this.fullName.getLastName(),
                this.email.asString(),
                this.groupId
        );
    }

    public static Optional<User> createFrom(Snapshot snapshot, Notification notification) {
        final Optional<EmailAddress> email = EmailAddress.email(snapshot.email, notification);
        final Optional<FullName> fullName = FullName.fullName(snapshot.firstName, snapshot.lastName, notification);
        if (notification.hasErrors()) {
            return Optional.empty();
        }
        return Optional.of(new User(
                snapshot.id,
                snapshot.name,
                fullName.get(),
                email.get(),
                snapshot.groupId
        ));
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return fullName.getFirstName();
    }

    public String getLastName() {
        return fullName.getLastName();
    }

    public EmailAddress getEmail() {
        return email;
    }

    public String getGroupId() {
        return groupId;
    }

    @SuppressWarnings("unused")
    public static class Snapshot {

        private final String id;

        private final String name;

        private final String firstName;

        private final String lastName;

        private final String email;

        private final String groupId;

        public Snapshot(String id, String name, String firstName, String lastName, String email, String groupId) {
            this.id = id;
            this.name = name;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.groupId = groupId;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getGroupId() {
            return groupId;
        }

    }
}
