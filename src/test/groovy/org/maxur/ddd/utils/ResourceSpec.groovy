package org.maxur.ddd.utils

import io.dropwizard.testing.DropwizardTestSupport
import io.dropwizard.testing.ResourceHelpers
import org.glassfish.jersey.client.JerseyClientBuilder
import org.maxur.ddd.Launcher
import spock.lang.Shared
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.xml.ws.Response

abstract class ResourceSpec extends Specification {

    abstract void setUpResources()

    @Shared
    DropwizardTestSupport<Launcher.AppConfiguration> SUPPORT =
        new DropwizardTestSupport<Launcher.AppConfiguration>(
            Launcher.class, ResourceHelpers.resourceFilePath("tddd.yml")
        );

    void setupSpec(){
        SUPPORT.before();
    }

    void cleanupSpec() {
        SUPPORT.after();
    }


    def loginHandlerRedirectsAfterPost() {
        when:
        Client client = new JerseyClientBuilder(SUPPORT.getEnvironment())
            .build("test client");

        then:
        Response response = client.target(
            String.format("http://localhost:%d/login", 8080))
            .request()
            .post(Entity.json(""));

        assert response.getStatus() == 302;
    }


}
