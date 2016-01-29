package org.maxur.ddd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.maxur.ddd.service.AccountDao;
import org.maxur.ddd.service.MailService;
import org.maxur.ddd.service.TeamDao;
import org.maxur.ddd.service.UserDao;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>04.11.2015</pre>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class User {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private String email;

    @JsonProperty
    private String teamId;

    @JsonProperty
    private String teamName;

    @JsonProperty
    private String password;

    private String encryptedPassword;

    public User() {
    }

    public User(String id, String name, String firstName, String lastName, String email, String teamId) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.teamId = teamId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public User create(UserDao userDao, TeamDao teamDao, AccountDao accountDao) throws BusinessException {
        validate();
        Team team = getTeam(teamDao);
        team.checkTeamCapacity(userDao);
        try {
            accountDao.save(this, team);
        } catch (RuntimeException e) {
            throw new BusinessException("Constrains violations");
        }
        this.encryptedPassword = encryptPassword();
        return this;
    }

    public void delete(TeamDao teamDao, AccountDao accountDao) throws BusinessException {
        final String teamId = getTeamId();
        Team team = getTeam(teamDao);
        try {
            accountDao.delete(id, team);
        } catch (RuntimeException e) {
            throw new BusinessException("Constrains violations");
        }
    }

    public User update(UserDao userDao, TeamDao teamDao, AccountDao accountDao) throws BusinessException {
        validate();
        Team team = getTeam(teamDao);
        team.checkTeamCapacity(userDao);
        try {
            accountDao.update(this, team);
        } catch (RuntimeException e) {
            throw new BusinessException("Constrains violations");
        }
        this.encryptedPassword = encryptPassword();
        return this;
    }

    public void changePassword(String password, UserDao userDao, MailService mailService) throws BusinessException {
        if (isNullOrEmpty(password)) {
            throw new BusinessException("User password must not be empty");
        }
        this.password = password;
        this.encryptedPassword = encryptPassword();
        userDao.changePassword(getId(), this.encryptedPassword);
    }

    private String encryptPassword() {
        if (this.password == null) {
            return null;
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        messageDigest.reset();
        messageDigest.update(this.password.getBytes());
        byte[] digest = messageDigest.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        while(hashtext.length() < 32 ){
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    private void validate() throws BusinessException {
        if (isNullOrEmpty(this.getId())) {
            throw new BusinessException("User Id must not be empty");
        }
        if (isNullOrEmpty(this.getName())) {
            throw new BusinessException("User Id must not be empty");
        }
        if (isNullOrEmpty(this.getFirstName())) {
            throw new BusinessException("User First Name must not be empty");
        }
        if (isNullOrEmpty(this.getLastName())) {
            throw new BusinessException("User Last Name must not be empty");
        }
        if (isNullOrEmpty(this.getEmail())) {
            throw new BusinessException("User Email must not be empty");
        }
        Matcher matcher = PATTERN.matcher(this.getEmail());
        if (!matcher.matches()) {
            throw new BusinessException("User Email is invalid");
        }
    }

    private Team getTeam(TeamDao teamDao) throws NotFoundException {
        Team team = teamDao.findById(this.teamId);
        if (team == null) {
            throw new NotFoundException("Team", this.teamId);
        }
        return team;
    }
}
