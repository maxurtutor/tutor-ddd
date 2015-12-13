package org.maxur.ddd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
public class User extends Entity {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    private String name;

    private String firstName;

    private String lastName;

    private String email;

    private String groupId;

    private String groupName;

    private User(String name, String firstName, String lastName, String email, String groupId, String groupName) {
        super();
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.groupId = groupId;
        this.groupName = groupName;
    }

    private User(String id, String name, String firstName, String lastName, String email, String groupId, String groupName) {
        super(id);
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public static User user(String id, String name, String firstName, String lastName, String email, String groupId, String groupName) {
        return new User(id, name, firstName, lastName, email, groupId, groupName);
    }

    public static User newUser(String name, String firstName, String lastName, String email, String groupId, String groupName) {
        return new User(name, firstName, lastName, email, groupId, groupName);
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

    public String getGroupName() {
        return groupName;
    }

    public void validate() throws ValidationException {
        if (firstName == null || firstName.isEmpty()) {
            throw new ValidationException("User First Name must not be empty");
        }
        if (lastName == null || lastName.isEmpty()) {
            throw new ValidationException("User Last Name must not be empty");
        }
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new ValidationException("User Email is invalid");
        }
    }
}
