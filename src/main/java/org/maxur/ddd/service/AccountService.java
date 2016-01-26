package org.maxur.ddd.service;

import org.maxur.ddd.dao.AccountDao;
import org.maxur.ddd.dao.TeamDao;
import org.maxur.ddd.dao.UserDao;
import org.maxur.ddd.domain.MailService;
import org.maxur.ddd.domain.Team;
import org.maxur.ddd.domain.Mail;
import org.maxur.ddd.domain.User;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.MessagingException;
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

    private final DBI dbi;

    private final MailService mailService;

    @Inject
    public AccountService(DBI dbi, MailService mailService) {
        this.dbi = dbi;
        this.mailService = mailService;
    }

    public User create(User user) throws BusinessException {
        validate(user);
        final String teamId = user.getTeamId();
        Team team = dbi.onDemand(TeamDao.class).findById(teamId);
        if (team == null) {
            throw new NotFoundException("Team", teamId);
        }
        Integer count = dbi.onDemand(UserDao.class).findCountByTeam(teamId);
        if (Objects.equals(count, team.getMaxCapacity())) {
            throw new BusinessException("The limit users in team is exceeded");
        }
        try {
            dbi.onDemand(AccountDao.class).save(user, team);
        } catch (RuntimeException e) {
            logger.error("Constrains violations", e);
            throw new BusinessException("Constrains violations");
        }

        final String message = format("You are included in the team '%s'", user.getTeamName());
        Mail mail = new Mail("TDDD System Notification", message, user.getEmail());
        try {
            mailService.send(mail);
        } catch (MessagingException e) {
            logger.error("Unable to send email", e);
        }
        return user;
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


}
