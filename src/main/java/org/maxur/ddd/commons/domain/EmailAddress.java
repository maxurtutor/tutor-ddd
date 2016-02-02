package org.maxur.ddd.commons.domain;

import com.google.common.base.Strings;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAddress {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    private final String email;

    public static EmailAddress email(String email) throws BusinessException {
        return new EmailAddress(checkEmail(email));
    }

    private EmailAddress(String email) {
        this.email = email;
    }

    private static String checkEmail(String email) throws BusinessException {
        if (Strings.isNullOrEmpty(email)) {
            throw new BusinessException("User Email must not be empty");
        }
        Matcher matcher = PATTERN.matcher(email);
        if (!matcher.matches()) {
            throw new BusinessException("User Email is invalid");
        }
        return email;
    }

    public String asString() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailAddress)) return false;
        EmailAddress that = (EmailAddress) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}