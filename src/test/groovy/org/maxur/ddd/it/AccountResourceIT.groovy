package org.maxur.ddd.it

import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.testing.DropwizardTestSupport
import org.maxur.ddd.Launcher
import org.maxur.ddd.infrastructure.view.UserDto
import spock.lang.Shared
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response
/**
 * @author myunusov
 * @version 1.0
 * @since <pre>06.01.2016</pre>
 */
class AccountResourceIT extends Specification {

    @Shared
    DropwizardTestSupport<Launcher.AppConfiguration> SUPPORT =
        new DropwizardTestSupport<Launcher.AppConfiguration>(
            Launcher.class, Launcher.class.getClassLoader().getResource("tddd.yml").toString()
        );

    void setupSpec(){
        SUPPORT.before();
    }

    void cleanupSpec() {
        SUPPORT.after();
    }

    def "should be find all users"() {
        given:
        Client client = new JerseyClientBuilder(SUPPORT.getEnvironment())
            .build("test1 client");
        when:
        Response response = client.target(
            String.format("http://localhost:%d/api/users", 8080))
            .request()
            .get();
        then:
        assert response.getStatus() == 200;
        List<UserDto> result = response.readEntity(new GenericType<List<UserDto>>() {});
        assert result.size() == 3
    }


    def "should be find all right users"() {
        expect:
        Client client = new JerseyClientBuilder(SUPPORT.getEnvironment())
            .build("test2." + index + " client");
        Response response = client.target(
            String.format("http://localhost:%d/api/users", 8080))
            .request()
            .get();
        List<UserDto> result = response.readEntity(new GenericType<List<UserDto>>() {});
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


    def "should be find user by id"() {
        expect:
        Client client = new JerseyClientBuilder(SUPPORT.getEnvironment())
            .build("test3." + index + " client");
        Response response = client.target(
            String.format("http://localhost:%d/api/users", 8080))
            .request()
            .get();
        List<UserDto> users = response.readEntity(new GenericType<List<UserDto>>() {});
        response.close()
        Response response1 = client.target(
            String.format("http://localhost:%d/api/users/" + users[index].id, 8080))
            .request()
            .get();
        UserDto result = response1.readEntity(UserDto.class)
        response1.close()

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


}
