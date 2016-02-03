package org.maxur.ddd.infrastructure.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.maxur.ddd.admin.domain.User;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>30.01.2016</pre>
 */
@SuppressWarnings("WeakerAccess")
public class UserDto {

    @JsonProperty
    public String id;

    @JsonProperty
    @NotEmpty
    public String name;

    @JsonProperty
    @NotEmpty
    public String firstName;

    @JsonProperty
    @NotEmpty
    public String lastName;

    @JsonProperty
    @NotEmpty
    @Email
    public String email;

    @JsonProperty
    @NotEmpty
    public String teamId;

    @JsonProperty
    public String teamName;

    @JsonProperty
    public String password;

    public static UserDto from(User user) {
        final UserDto result = new UserDto();
        result.id = user.getId().asString();
        result.name = user.getName();
        result.firstName = user.getFirstName();
        result.lastName = user.getLastName();
        result.email = user.getEmail();
        result.teamId = user.getTeamId().asString();
        result.teamName = user.getTeamName();
        return result;
    }

}
