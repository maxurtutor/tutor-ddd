package org.maxur.ddd.it

import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.testing.DropwizardTestSupport
import org.glassfish.jersey.client.ClientProperties
import org.maxur.ddd.RestApp
import org.maxur.ddd.config.Config
import org.maxur.ddd.domain.User
import spock.lang.Shared
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.core.Response

import static java.lang.String.format

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>06.01.2016</pre>
 */
class AccountResourceIT extends Specification {

    @Shared
    DropwizardTestSupport<Config> SUPPORT =
        new DropwizardTestSupport<Config>(
            RestApp.class, RestApp.class.getClassLoader().getResource("dropwizard.yml").toString()
        )

    @Shared
    Client client

    void setupSpec() {
        SUPPORT.before();
        client = new JerseyClientBuilder(SUPPORT.getEnvironment())
            .build("test client")
        client.property(ClientProperties.CONNECT_TIMEOUT, 100);
        client.property(ClientProperties.READ_TIMEOUT, 10000);
    }

    void cleanupSpec() {
        client.close()
        SUPPORT.after();
    }

    void setup() {
    }

    def "should be return null current user without authentication"()
    {
        given:
        Response response = client.target(
            format("http://localhost:%d/api/users/me", 8080))
            .request()
            .get();
        User result = response.readEntity(User)
        expect:
        assert response.getStatus() == 204;
        and:
        assert result == null
    }
}
