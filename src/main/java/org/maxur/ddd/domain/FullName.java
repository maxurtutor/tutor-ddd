package org.maxur.ddd.domain;

import java.util.Optional;

public class FullName {

    private final String firstName;

    private final String lastName;

    private FullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Optional<FullName> fullName(String firstName, String lastName, Notification notification) {
        if (firstName == null || firstName.isEmpty()) {
            notification.addError("User First Name must not be empty");
        }
        if (lastName == null || lastName.isEmpty()) {
            notification.addError("User Last Name must not be empty");
        }
        return notification.hasErrors() ?
                Optional.empty() :
                Optional.of(new FullName(firstName, lastName));
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}