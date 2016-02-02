package org.maxur.ddd.planning.domain;

import org.maxur.ddd.commons.domain.BusinessException;
import org.maxur.ddd.commons.domain.EmailAddress;
import org.maxur.ddd.commons.domain.Person;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/2/2016</pre>
 */
public class TeamMember {

    private final Person person;

    public TeamMember(Person person) {
        this.person = person;
    }

    public static TeamMember restore(TeamMember.Snapshot snapshot) throws BusinessException {
        Person person = Person.person(snapshot.firstName, snapshot.lastName, EmailAddress.email(snapshot.email));
        return new TeamMember(person);
    }


    public TeamMember.Snapshot getSnapshot() {
        TeamMember.Snapshot snapshot = new Snapshot();
        snapshot.setFirstName(person.getFirstName());
        snapshot.setLastName(person.getLastName());
        snapshot.setEmail(person.getEmailAddress().asString());
        return  snapshot;
    }

    public String getFullName() {
        return format("%s %s", person.getFirstName(), person.getLastName());
    }

    public String getEmail() {
        return person.getEmailAddress().asString();
    }

    @SuppressWarnings("unused")
    public class Snapshot {
        private String firstName;
        private String lastName;
        private String email;

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
    }
}
