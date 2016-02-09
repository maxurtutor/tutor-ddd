package org.maxur.ddd.ui;

import io.swagger.annotations.Contact;
import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Swagger;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>03.02.2016</pre>
 */
@SwaggerDefinition(
    info = @Info(
        description = "DDD Tutorial Project",
        version = "V0.1",
        title = "T-DDD",
        contact = @Contact(
            name = "Maxim Yunusov",
            email = "myunusov@maxur.org",
            url = "http://www.maxur.org"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "http://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    consumes = {"application/json", "application/xml"},
    produces = {"application/json", "application/xml"},
    schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
    externalDocs = @ExternalDocs(value = "GitHub", url = "https://github.com/myunusov/tutor-ddd")
)
public class ApiDefinition implements ReaderListener {

    public void beforeScan(Reader reader, Swagger swagger) {
    }

    public void afterScan(Reader reader, Swagger swagger) {
        swagger.setBasePath("/api");
    }
}