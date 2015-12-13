package org.maxur.ddd.domain;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>13.12.2015</pre>
 */
public class EmailAddress {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    private final String address;

    private EmailAddress(String address) {
        this.address = address;
    }

    public static Optional<EmailAddress> email(String address, Notification notification) {
        Matcher matcher = PATTERN.matcher(address);
        if (!matcher.matches()) {
            notification.addError("Email Address is invalid");
            return Optional.empty();
        }
        return Optional.of(new EmailAddress(address));
    }

    public String asString() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailAddress)) return false;
        EmailAddress that = (EmailAddress) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public String toString() {
        return "EmailAddress{" +
                "address='" + address + '\'' +
                '}';
    }
}
