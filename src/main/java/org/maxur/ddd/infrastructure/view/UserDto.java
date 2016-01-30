package org.maxur.ddd.infrastructure.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.maxur.ddd.domain.BusinessException;
import org.maxur.ddd.domain.User;

import static org.maxur.ddd.domain.User.make;

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
    public String name;

    @JsonProperty
    public String firstName;

    @JsonProperty
    public String lastName;

    @JsonProperty
    public String email;

    @JsonProperty
    public String teamId;

    @JsonProperty
    public String teamName;

    @JsonProperty
    public String password;

    public User assemble() throws BusinessException {
        return make(id, name, firstName, lastName, email, teamId);
    }

    public static UserDto from(User user) {
        final UserDto result = new UserDto();
        result.id = user.getId();
        result.name = user.getName();
        result.firstName = user.getFirstName();
        result.lastName = user.getLastName();
        result.email = user.getEmail();
        result.teamId = user.getTeamId();
        result.teamName = user.getTeamName();
        return result;
    }

}
