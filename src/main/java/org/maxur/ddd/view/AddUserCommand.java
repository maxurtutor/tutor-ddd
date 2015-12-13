package org.maxur.ddd.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.maxur.ddd.domain.*;

import java.util.Optional;

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
        Notification notification = new Notification();
        final Optional<EmailAddress> email = EmailAddress.email(this.email, notification);
        final Optional<FullName> fullName = FullName.fullName(this.firstName, this.lastName, notification);
        if (notification.hasErrors()) {
            throw new ValidationException(notification.errorMessage());
        }
        return User.newUser(name, fullName.get(), email.get(), groupId);
    }

}
