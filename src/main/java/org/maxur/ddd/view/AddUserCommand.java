package org.maxur.ddd.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.domain.ValidationException;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>13.12.2015</pre>
 */
public class AddUserCommand {

    @JsonProperty
    public String name;

    @JsonProperty
    public String firstName;

    @JsonProperty
    public String lastName;

    @JsonProperty
    public String email;

    @JsonProperty
    public String groupId;

    public User assemble() throws ValidationException {
       return User.newUser(name, firstName, lastName, email, groupId);
    }

}
