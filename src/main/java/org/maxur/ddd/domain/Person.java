package org.maxur.ddd.domain;

import com.google.common.base.Strings;

import java.util.Objects;

public class Person {

    private final String firstName;

    private final String lastName;

    public static Person make(String firstName, String lastName) throws BusinessException {
        return new Person(checkFirstName(firstName), checkLastName(lastName));
    }

    private Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(firstName, person.firstName) &&
                Objects.equals(lastName, person.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
}