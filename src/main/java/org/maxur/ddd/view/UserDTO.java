package org.maxur.ddd.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.maxur.ddd.domain.User;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>13.12.2015</pre>
 */
public class UserDTO {

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

    public User assemble() {
        return id == null ?
                User.newUser(name, firstName, lastName, email, groupId, groupName) :
                User.user(id, name, firstName, lastName, email, groupId, groupName);
    }

    public static UserDTO dto(User user) {
        final UserDTO dto = new UserDTO();
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
