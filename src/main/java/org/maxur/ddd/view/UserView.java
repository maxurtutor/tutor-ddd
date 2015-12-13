package org.maxur.ddd.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.maxur.ddd.domain.User;
import org.maxur.ddd.domain.ValidationException;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>13.12.2015</pre>
 */
public class UserView {

    @JsonProperty
    public String id;

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

    @JsonProperty
    public String groupName;

    public static UserView view(User user) {
        final UserView dto = new UserView();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.firstName = user.getFirstName();
        dto.lastName = user.getLastName();
        dto.email = user.getEmail();
        dto.groupId = user.getGroupId();
        dto.groupName = user.getGroupName();
        return dto;
    }
}
