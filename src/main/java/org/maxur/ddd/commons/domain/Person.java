package org.maxur.ddd.commons.domain;

import com.google.common.base.Strings;

import java.util.Objects;

public class Person {

    private final String firstName;

    private final String lastName;

    private final EmailAddress emailAddress;

    public static Person person(String firstName, String lastName, EmailAddress email) throws BusinessException {
        return new Person(checkFirstName(firstName), checkLastName(lastName), email);
    }

    private Person(String firstName, String lastName, EmailAddress email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = email;
    }

    private static String checkFirstName(String firstName) throws BusinessException {
        if (Strings.isNullOrEmpty(firstName)) {
            throw new BusinessException("User First Name must not be empty");
        }
        return firstName;
    }

    private static String checkLastName(String lastName) throws BusinessException {
        if (Strings.isNullOrEmpty(lastName)) {
            throw new BusinessException("User Last Name must not be empty");
        }
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(firstName, person.firstName) &&
                Objects.equals(lastName, person.lastName) &&
                Objects.equals(emailAddress, person.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, emailAddress);
    }
}