package org.maxur.ddd.it

import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.testing.DropwizardTestSupport
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature
import org.maxur.ddd.RestApp
import org.maxur.ddd.config.Config
import org.maxur.ddd.domain.User
import spock.lang.Shared
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response

import static java.lang.String.format
/**
 * @author myunusov
 * @version 1.0
 * @since <pre>06.01.2016</pre>
 */
class AccountResourceIT extends Specification {

    public static final int PORT = 8080

    public static final String CONFIG = "dropwizard-test.yml"

    @Shared
    DropwizardTestSupport<Config> SUPPORT =
        new DropwizardTestSupport<Config>(
            RestApp.class, RestApp.class.getClassLoader().getResource(CONFIG).toString()
        )

    @Shared
    Client user

    @Shared
    Client anonym


    void setupSpec() {
        SUPPORT.before();

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().build();
        user = new JerseyClientBuilder(SUPPORT.getEnvironment())
            .build("test authorised client")
        user.property(ClientProperties.CONNECT_TIMEOUT, 100);
        user.property(ClientProperties.READ_TIMEOUT, 10000);
        user.register(feature);

        anonym = new JerseyClientBuilder(SUPPORT.getEnvironment())
            .build("test unauthorised client")
        anonym.property(ClientProperties.CONNECT_TIMEOUT, 100);
        anonym.property(ClientProperties.READ_TIMEOUT, 10000);
    }

    void cleanupSpec() {
        anonym.close()
        user.close()
        SUPPORT.after();
    }

    void setup() {
    }

    def "System should return current user as null without authentication"() {
        when: "Somebody requests current user info"
        Response response = anonym.target(
            format("http://localhost:%d/api/users/me", PORT))
            .request()
            .get();
        then: "System returns status 'No Content (204)'"
        assert response.getStatus() == 401;
    }

    def "System should return error on request all users without authentication"() {
        when: "Somebody requests all users info"
        Response response = anonym.target(
                format("http://localhost:%d/api/users", PORT))
                .request()
                .get();
        then: "System returns status 'Unauthorized (401)'"
        assert response.getStatus() == 401;
    }

    def "System should return all users"() {
        given: "Admin requests all users info"
        Response response = user.target(
                format("http://localhost:%d/api/users", PORT))
                .request()
                .header("Authorization", "Bearer test")
                .get();
        List<User> result = response.readEntity(new GenericType<List<User>>() {});
        expect:  "System returns status 'OK (200)'"
        assert response.getStatus() == 200;
        and: "System returns 3 users"
        assert result != null
        assert result.size() == 3
        assert result[index].email == email
        assert result[index].password == password
        assert result[index].roles == roles
        where:
        index || email            | password | roles
        0     || "ivan@mail.com"  | "p1"     | ['ADMIN', 'USER']
        1     || "petr@mail.com"  | "p2"     | ['USER']
        2     || "sidor@mail.com" | "p3"     | []
    }

    def "System should return error on request user by identifier without authentication"() {
        when: "Somebody requests user info by it's identifier"
        Response response = anonym.target(
                format("http://localhost:%d/api/users/u1", PORT))
                .request()
                .get();
        then: "System returns status 'Unauthorized (401)'"
        assert response.getStatus() == 401;
    }

    def "System should return user by it's identifier"() {
        given: "Admin requests user info by it's identifier"
        Response response = user.target(
                format("http://localhost:%d/api/users/" + id, PORT))
                .request()
                .header("Authorization", "Bearer test")
                .get();
        User result = response.readEntity(User)
        expect: "System returns status 'OK (200)'"
        assert response.getStatus() == 200;
        and: "System returns valid user's info"
        assert result != null
        assert result.email == email
        assert result.password == password
        assert result.roles == roles
        where:
        id   ||  email            | password | roles
        "u1" ||  "ivan@mail.com"  | "p1"     | ['ADMIN', 'USER']
        "u2" ||  "petr@mail.com"  | "p2"     | ['USER']
        "u3" ||  "sidor@mail.com" | "p3"     | []
    }

    def "System should authenticate user by it's email and password"() {
        when: "User requests authentication by it's email and password and requests all users"
        Response response = user.target(
                format("http://localhost:%d/api/users", PORT))
                .request()
                .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, "ivan@mail.com")
                .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, "p1")
                .get();
        then:  "System returns status 'OK (200)'"
        assert response.getStatus() == 200;
        and: "System returns 3 users"
        List<User> result = response.readEntity(new GenericType<List<User>>() {});
        assert result != null
        assert result.size() == 3
    }

    def "System should forbid access for not authorised user"() {
        when: "User requests authentication by it's email and password and requests all users"
        Response response = user.target(
                format("http://localhost:%d/api/users", PORT))
                .request()
                .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, "petr@mail.com")
                .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, "p2")
                .get();
        then: "System returns status 'Forbidden (403)'"
        assert response.getStatus() == 403;
    }

}
