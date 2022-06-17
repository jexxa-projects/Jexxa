package io.jexxa.infrastructure.drivingadapter.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.jexxa.TestConstants;
import io.jexxa.application.applicationservice.Java8DateTimeApplicationService;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static io.jexxa.infrastructure.drivingadapter.rest.JexxaWebProperties.JEXXA_REST_OPEN_API_PATH;
import static io.jexxa.infrastructure.drivingadapter.rest.JexxaWebProperties.JEXXA_REST_PORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class OpenAPIJava8DateTimeTest
{
    private static final String OPENAPI_PATH = "http://localhost:7500/swagger-docs/";
    private final Java8DateTimeApplicationService java8DateTimeApplicationService = new Java8DateTimeApplicationService();


    private RESTfulRPCAdapter objectUnderTest;

    @BeforeEach
    void setupTests(){
        //Setup
        var properties = new Properties();
        var defaultHost = "localhost";
        var defaultPort = 7500;

        properties.put(JexxaWebProperties.JEXXA_REST_HOST, defaultHost);
        properties.put(JEXXA_REST_PORT, Integer.toString(defaultPort));
        properties.put(JEXXA_REST_OPEN_API_PATH, "swagger-docs");

        objectUnderTest = RESTfulRPCAdapter.createAdapter(properties);
        objectUnderTest.register(java8DateTimeApplicationService);
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
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
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
        JsonObject openAPI = Unirest.get(OPENAPI_PATH)
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .asObject(JsonObject.class).getBody();

        //Act
        var result = deepSearchKeys(openAPI, "content");

        //Assert - Fields of basic openAPI structure
        assertFalse( result.isEmpty() );
        result.forEach( element -> assertNotNull( element.getAsJsonObject().get(RESTConstants.APPLICATION_TYPE) ) );
    }

   @Test
    void testHTTPRequestMapping()
    {
        //Arrange
        var resTfulRPCConvention = new RESTfulRPCConvention(java8DateTimeApplicationService);

        JsonObject openAPI = Unirest.get(OPENAPI_PATH)
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .asObject(JsonObject.class).getBody();

        //Act
        var postResults = deepSearchKeys(openAPI, "post");
        var getResults = deepSearchKeys(openAPI, "get");

        //Assert -- POST mapping
        assertFalse(postResults.isEmpty());
        assertEquals(resTfulRPCConvention.getPOSTCommands().size(), postResults.size());

        //Assert -- GET mapping
        assertFalse(getResults.isEmpty());
        assertEquals(resTfulRPCConvention.getGETCommands().size(), getResults.size());
    }

    private List<JsonElement> deepSearchKeys(JsonElement jsonElement, String key)
    {
        List<JsonElement> result = new ArrayList<>();
        deepSearchKeys(jsonElement, key, result);
        return result;
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
