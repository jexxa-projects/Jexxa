package io.jexxa.infrastructure.drivingadapter.rest;

import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter.HTTP_PORT_PROPERTY;
import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter.OPEN_API_PATH;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.jexxa.TestConstants;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class OpenAPITest
{
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_TYPE = "application/json";

    private static final String OPENAPI_PATH = "http://localhost:7000/swagger-docs/";
    private final SimpleApplicationService simpleApplicationService = new SimpleApplicationService();


    private RESTfulRPCAdapter objectUnderTest;

    @BeforeEach
    void setupTests(){
        //Setup
        var properties = new Properties();
        var defaultHost = "localhost";
        var defaultPort = 7000;

        properties.put(RESTfulRPCAdapter.HOST_PROPERTY, defaultHost);
        properties.put(HTTP_PORT_PROPERTY, Integer.toString(defaultPort));
        properties.put(OPEN_API_PATH, "swagger-docs");

        objectUnderTest = new RESTfulRPCAdapter(properties);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();
    }

    @AfterEach
    void tearDownTests(){
        //tear down
        objectUnderTest.stop();
        objectUnderTest = null;
        Unirest.shutDown();
    }


    @Test
    void testBasicStructure()
    {
        //Arrange -> Nothing to do

        //Act
        JsonObject result = Unirest.get(OPENAPI_PATH)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(JsonObject.class).getBody();

        //Assert - Fields of basic openAPI structure
        assertNotNull(result);

        assertNotNull(result.get("openapi"));
        assertNotNull(result.get("info"));
        assertNotNull(result.get("paths"));
    }

    @Test
    void testContentTypeIsJson()
    {
        //Arrange
        var result = new ArrayList<JsonElement>();

        JsonObject openAPI = Unirest.get(OPENAPI_PATH)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(JsonObject.class).getBody();

        //Act
        deepSearchKeys(openAPI, "content", result);

        //Assert - Fields of basic openAPI structure
        assertFalse( result.isEmpty() );
        result.forEach( element -> assertNotNull( element.getAsJsonObject().get(APPLICATION_TYPE) ) );
    }


    private void deepSearchKeys(JsonElement jsonElement, String key, List<JsonElement> result)
    {
        Objects.requireNonNull(jsonElement);

        if ( jsonElement.isJsonObject() )
        {
            jsonElement.getAsJsonObject().entrySet().forEach(element -> {
                if ( element.getKey().equals(key) ) {
                    result.add(element.getValue());
                }
                deepSearchKeys( element.getValue(), key, result);
            });
        }
    }

}
