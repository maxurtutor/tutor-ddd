package org.maxur.ddd.infrastructure.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.maxur.ddd.admin.domain.User;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>30.01.2016</pre>
 */
@SuppressWarnings("WeakerAccess")
@ApiModel(value = "UserDetails")
public class UserDto {

    @JsonProperty
    public String id;

    @JsonProperty
    @NotEmpty
    @ApiModelProperty(value = "The User's name.", required = true)
    public String name;

    @JsonProperty
    @NotEmpty
    @ApiModelProperty(value = "The User's first name.", required = true)
    public String firstName;

    @JsonProperty
    @NotEmpty
    @ApiModelProperty(value = "The User's last name.", required = true)
    public String lastName;

    @JsonProperty
    @NotEmpty
    @Email
    @ApiModelProperty(value = "The User's email.", required = true)
    public String email;

    @JsonProperty
    @NotEmpty
    @ApiModelProperty(value = "The User's team identifier.", required = true)
    public String teamId;

    @JsonProperty
    public String teamName;

    @JsonProperty
    @ApiModelProperty(value = "The User's password.", required = false)
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
