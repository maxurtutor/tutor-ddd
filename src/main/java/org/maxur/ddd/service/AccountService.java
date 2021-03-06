package org.maxur.ddd.service;

import org.maxur.ddd.domain.Mail;
import org.maxur.ddd.domain.Team;
import org.maxur.ddd.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class AccountService {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private Logger logger = LoggerFactory.getLogger(AccountService.class);

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    private final UserDao userDao;

    private final TeamDao teamDao;

    private final AccountDao accountDao;

    private final MailService mailService;

    @Inject
    public AccountService(UserDao userDao, TeamDao teamDao, AccountDao accountDao, MailService mailService) {
        this.userDao = userDao;
        this.teamDao = teamDao;
        this.accountDao = accountDao;
        this.mailService = mailService;
    }

    public User create(User user) throws BusinessException {
        validate(user);
        final String teamId = user.getTeamId();
        Team team = getTeam(teamId);
        Integer count = userDao.findCountByTeam(teamId);
        if (Objects.equals(count, team.getMaxCapacity())) {
            throw new BusinessException("The limit users in team is exceeded");
        }
        try {
            accountDao.save(user, team);
        } catch (RuntimeException e) {
            throw new BusinessException("Constrains violations");
        }

        final String message = format("Welcome to team '%s' !", user.getTeamName());
        sendMessage(user, message);
        user.setPassword(encryptPassword(user.getPassword()));
        return user;
    }

    public void delete(String id) throws BusinessException {

        final User user = getUser(id);

        final String teamId = user.getTeamId();
        Team team = getTeam(teamId);

        try {
            accountDao.delete(id, team);
        } catch (RuntimeException e) {
            throw new BusinessException("Constrains violations");
        }

        final String message = "Good by!";
        sendMessage(user, message);
        user.setPassword(encryptPassword(user.getPassword()));
    }

    public User update(User user) throws BusinessException {
        validate(user);
        final String teamId = user.getTeamId();
        Team team = getTeam(teamId);
        Integer count = userDao.findCountByTeam(teamId);
        if (Objects.equals(count, team.getMaxCapacity())) {
            throw new BusinessException("The limit users in team is exceeded");
        }
        try {
            accountDao.update(user, team);
        } catch (RuntimeException e) {
            throw new BusinessException("Constrains violations");
        }

        final String message = format("Welcome to team '%s' !", user.getTeamName());
        sendMessage(user, message);
        user.setPassword(encryptPassword(user.getPassword()));
        return user;
    }

    public User findById(String id) throws NotFoundException {
        return getUser(id);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public void changePassword(String id, String password) throws BusinessException {
        if (isNullOrEmpty(id)) {
            throw new BusinessException("User Id must not be empty");
        }
        if (isNullOrEmpty(password)) {
            throw new BusinessException("User password must not be empty");
        }

        final User user = getUser(id);

        userDao.changePassword(id, encryptPassword(password));

        final String message = "You password has been changed";
        sendMessage(user, message);
    }

    private String encryptPassword(String password) {
        if (password == null) {
            return null;
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        messageDigest.reset();
        messageDigest.update(password.getBytes());
        byte[] digest = messageDigest.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        while(hashtext.length() < 32 ){
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    private void validate(User user) throws BusinessException {
        if (isNullOrEmpty(user.getId())) {
            throw new BusinessException("User Id must not be empty");
        }
        if (isNullOrEmpty(user.getName())) {
            throw new BusinessException("User Id must not be empty");
        }
        if (isNullOrEmpty(user.getFirstName())) {
            throw new BusinessException("User First Name must not be empty");
        }
        if (isNullOrEmpty(user.getLastName())) {
            throw new BusinessException("User Last Name must not be empty");
        }
        if (isNullOrEmpty(user.getEmail())) {
            throw new BusinessException("User Email must not be empty");
        }
        Matcher matcher = pattern.matcher(user.getEmail());
        if (!matcher.matches()) {
            throw new BusinessException("User Email is invalid");
        }
    }

    private User getUser(String id) throws NotFoundException {
        final User user = userDao.findById(id);
        if (user == null) {
            throw new NotFoundException("User", id);
        }
        return user;
    }

    private Team getTeam(String teamId) throws NotFoundException {
        Team team = teamDao.findById(teamId);
        if (team == null) {
            throw new NotFoundException("Team", teamId);
        }
        return team;
    }

    private void sendMessage(User user, String message) {
        Mail mail = new Mail("TDDD System Notification", message, user.getEmail());
        try {
            mailService.send(mail);
        } catch (MessagingException e) {
            logger.error("Unable to send email", e);
        }
    }
}
