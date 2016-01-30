package org.maxur.ddd.domain;

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
@SuppressWarnings("unused")
public class User {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    private String id;

    private String name;

    private String firstName;

    private String lastName;

    private String email;

    private String teamId;

    private String teamName;

    private String password;

    private String encryptedPassword;

    private User() {
    }

    public static User make(
            String id, String name, String firstName, String lastName, String email, String teamId
    ) throws BusinessException {
        final User user = new User();
        user.setId(id);
        user.setName(name);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setTeamId(teamId);
        return user;
    }

    public static User restore(Snapshot snapshot) throws BusinessException {
        final User user = new User();
        user.setId(snapshot.id);
        user.setName(snapshot.name);
        user.setFirstName(snapshot.firstName);
        user.setLastName(snapshot.lastName);
        user.setEmail(snapshot.email);
        user.setTeamId(snapshot.teamId);
        user.setEncryptedPassword(snapshot.password);
        return user;
    }

    public Snapshot getSnapshot() {
        final Snapshot snapshot = new Snapshot();
        snapshot.id = this.id;
        snapshot.name = this.name;
        snapshot.firstName = this.firstName;
        snapshot.lastName = this.lastName;
        snapshot.email = this.email;
        snapshot.teamId = this.teamId;
        snapshot.teamName = this.teamName;
        snapshot.password = this.encryptedPassword;
        return snapshot;
    }

    public String getId() {
        return id;
    }
    private void setId(String id) throws BusinessException {
        if (isNullOrEmpty(id)) {
            throw new BusinessException("User Id must not be empty");
        }
        this.id = id;
    }
    private void setName(String name) throws BusinessException {
        if (isNullOrEmpty(name)) {
            throw new BusinessException("User Name must not be empty");
        }
        this.name = name;
    }
    private void setFirstName(String firstName) throws BusinessException {
        if (isNullOrEmpty(firstName)) {
            throw new BusinessException("User First Name must not be empty");
        }
        this.firstName = firstName;
    }
    private void setLastName(String lastName) throws BusinessException {
        if (isNullOrEmpty(lastName)) {
            throw new BusinessException("User Last Name must not be empty");
        }
        this.lastName = lastName;
    }
    private void setEmail(String email) throws BusinessException {
        if (isNullOrEmpty(email)) {
            throw new BusinessException("User Email must not be empty");
        }
        Matcher matcher = PATTERN.matcher(email);
        if (!matcher.matches()) {
            throw new BusinessException("User Email is invalid");
        }
        this.email = email;
    }
    private void setTeamId(String teamId) {
        this.teamId = teamId;
    }
    private void setPassword(String password) {
        this.password = password;
    }

    private void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
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

    public String getTeamId() {
        return teamId;
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

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public User create(Team team, UserDao userDao) throws BusinessException {
        team.checkTeamCapacity(userDao);
        this.encryptedPassword = encryptPassword();
        return this;
    }

    public User update(User old, Team team, UserDao userDao) throws BusinessException {
        if (!old.getTeamId().equals(teamId)) {
            team.checkTeamCapacity(userDao);
        }
        this.encryptedPassword = encryptPassword();
        return this;
    }

    public void changePassword(String password) throws BusinessException {
        if (isNullOrEmpty(password)) {
            throw new BusinessException("User password must not be empty");
        }
        this.password = password;
        this.encryptedPassword = encryptPassword();
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

    public static class Snapshot {
        public String id;
        public String name;
        public String firstName;
        public String lastName;
        public String email;
        public String teamId;
        public String teamName;
        public String password;
    }
}
