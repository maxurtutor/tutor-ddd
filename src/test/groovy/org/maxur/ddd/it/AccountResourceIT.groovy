package org.maxur.ddd.it

import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.testing.DropwizardTestSupport
import org.glassfish.jersey.client.ClientProperties
import org.maxur.ddd.TdddApp
import org.maxur.ddd.config.Config
import org.maxur.ddd.infrastructure.view.UserDto
import spock.lang.Shared
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response

import static org.maxur.ddd.utils.DBUtils.runScript

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>06.01.2016</pre>
 */
class AccountResourceIT extends Specification {

    def static teamIds = ['56ae13d3efa1681bd099e177', '56ae13d3efa1681bd099e178']
    def static userIds = ['56ae13d3efa1681bd099e179', '56ae13d3efa1681bd099e180', '56ae13d3efa1681bd099e181']

    @Shared
    DropwizardTestSupport<Config> SUPPORT =
        new DropwizardTestSupport<Config>(
            TdddApp.class, TdddApp.class.getClassLoader().getResource("tddd.yml").toString()
        )

    @Shared
    Client client

    void setupSpec(){
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
        runScript("/test.dml");
    }

    def "should be find all users details" () {
        given:
        Response response = client.target(
                String.format("http://localhost:%d/api/users", 8080))
                .request()
                .get();
        List<UserDto> result = response.readEntity(new GenericType<List<UserDto>>() {});
        expect:
        assert response.getStatus() == 200;
        assert result != null
        and:
        assert result.size() == 3
        and:
        result[index].name == name
        result[index].firstName == firstName
        result[index].lastName == lastName
        result[index].email == email
        result[index].teamName == teamName
        result[index].password == password
        where:
        index || name    | firstName | lastName   | email            | teamName | password
        0     || "iv"    | "Ivan"    | "Ivanov"   | "ivan@mail.com"  | "T_DDD"  | null
        1     || "petr"  | "Petr"    | "Petrov"   | "petr@mail.com"  | "T_DDD"  | null
        2     || "sidor" | "Sidor"   | "Sidorov"  | "sidor@mail.com" | "T_CQRS" | null
    }

    def "should be find user by it's id"() {
        given:
        Response response = client.target(
                String.format("http://localhost:%d/api/users/" + userIds[index], 8080))
                .request()
                .get();
        UserDto result = response.readEntity(UserDto.class)
        expect:
        assert response.getStatus() == 200;
        and:
        assert result != null
        and:
        result.name == name
        result.firstName == firstName
        result.lastName == lastName
        result.email == email
        result.teamName == teamName
        result.password == password
        where:
        index || name    | firstName | lastName   | email            | teamName | password
        0     || "iv"    | "Ivan"    | "Ivanov"   | "ivan@mail.com"  | "T_DDD"  | null
        1     || "petr"  | "Petr"    | "Petrov"   | "petr@mail.com"  | "T_DDD"  | null
        2     || "sidor" | "Sidor"   | "Sidorov"  | "sidor@mail.com" | "T_CQRS" | null
    }

    def "should be update users personal data"() {
        given:
        def dto = new UserDto();
        dto.name = "1"
        dto.firstName = "2"
        dto.lastName = "3"
        dto.email = "ab@cd.ef"
        dto.teamId = "56ae13d3efa1681bd099e177"
        when:
        Response response1 = client.target(
                String.format("http://localhost:%d/api/users/" + userIds[0], 8080))
                .request()
                .put(Entity.json(dto));
        Response response2 = client.target(
                String.format("http://localhost:%d/api/users/" + userIds[0], 8080))
                .request()
                .get();
        UserDto result = response2.readEntity(UserDto.class)
        then:
        assert response1.getStatus() == 200;
        assert response2.getStatus() == 200;
        and:
        assert result != null
        and:
        result.name == "iv"
        result.firstName == "2"
        result.lastName == "3"
        result.email == "ab@cd.ef"
        result.teamName == "T_DDD"
        result.password == null
    }

    def "should be update users personal data if it's valid "() {
        given:
        def dto = new UserDto();
        dto.name = name
        dto.firstName = firstName
        dto.lastName = lastName
        dto.email = email
        dto.teamId = teamId
        Response response = client.target(
            String.format("http://localhost:%d/api/users/" + userIds[0], 8080))
            .request()
            .put(Entity.json(dto));
        expect:
        response.getStatus() == resultCode

        where:
         name    | firstName | lastName   | email            | teamId      ||  resultCode
         "iv"    | "Ivan"    | "Ivanov"   | "ivan@mail.com"  | teamIds[0]  ||  200
         null    | "Ivan"    | "Ivanov"   | "ivan@mail.com"  | teamIds[0]  ||  422
         ""      | "Ivan"    | "Ivanov"   | "ivan@mail.com"  | teamIds[0]  ||  422
         "iv"    | null      | "Ivanov"   | "ivan@mail.com"  | teamIds[0]  ||  422
         "iv"    | ""        | "Ivanov"   | "ivan@mail.com"  | teamIds[0]  ||  422
         "iv"    | "Ivan"    | null       | "ivan@mail.com"  | teamIds[0]  ||  422
         "iv"    | "Ivan"    | ""         | "ivan@mail.com"  | teamIds[0]  ||  422
         "iv"    | "Ivan"    | "Ivanov"   | "invalid"        | teamIds[0]  ||  422
         "iv"    | "Ivan"    | "Ivanov"   | null             | teamIds[0]  ||  422
         "iv"    | "Ivan"    | "Ivanov"   | ""               | teamIds[0]  ||  422
         "iv"    | "Ivan"    | "Ivanov"   | "ivan@mail.com"  | null        ||  422
         "iv"    | "Ivan"    | "Ivanov"   | "ivan@mail.com"  | ""          ||  422
         "iv"    | "Ivan"    | "Ivanov"   | "ivan@mail.com"  | "invalid"   ||  400
    }


    def "should be change users password"() {
        given:
        def dto = new UserDto();
        dto.name = "iv"
        dto.firstName = "2"
        dto.lastName = "3"
        dto.email = "ab@cd.ef"
        dto.teamId = "56ae13d3efa1681bd099e177"
        dto.password = "password";
        when:
        Response response1 = client.target(
                String.format("http://localhost:%d/api/users/" + userIds[0] + "/password", 8080))
                .request()
                .put(Entity.json(dto));
        Response response2 = client.target(
                String.format("http://localhost:%d/api/users/" + userIds[0], 8080))
                .request()
                .get();
        UserDto result = response2.readEntity(UserDto.class)
        then:
        assert response1.getStatus() == 200;
        assert response2.getStatus() == 200;
        and:
        assert result != null
        and:
        result.name == "iv"
        result.firstName == "Ivan"
        result.lastName == "Ivanov"
        result.email == "ivan@mail.com"
        result.teamName == "T_DDD"
        result.password == null
    }

    def "should be change users password if it's valid "() {
        given:
        def dto = new UserDto();
        dto.name = name
        dto.firstName = firstName
        dto.lastName = lastName
        dto.email = email
        dto.teamId = teamId
        dto.password = password
        Response response = client.target(
            String.format("http://localhost:%d/api/users/" + userIds[0] + "/password", 8080))
            .request()
            .put(Entity.json(dto));
        expect:
        response.getStatus() == resultCode

        where:
        name    | firstName | lastName   | email            | teamId     | password   ||  resultCode
        "iv"    | "Ivan"    | "Ivanov"   | "ivan@mail.com"  | teamIds[0] | "password" ||  200
        "iv"    | "Ivan"    | "Ivanov"   | "ivan@mail.com"  | teamIds[0] | null       ||  422
        "iv"    | "Ivan"    | "Ivanov"   | "ivan@mail.com"  | teamIds[0] | ""         ||  422
    }


    def "should be returns current user"() {
        given:
        Response response = client.target(
            String.format("http://localhost:%d/api/users/me", 8080))
            .request()
            .get();
        UserDto result = response.readEntity(UserDto.class)
        expect:
        assert response.getStatus() == 204;
        and:
        assert result == null
    }


}
