package io.jexxa.drivingadapter.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.jexxa.TestConstants;
import io.jexxa.testapplication.applicationservice.Java8DateTimeApplicationService;
import io.jexxa.testapplication.applicationservice.SimpleApplicationService;
import io.jexxa.testapplication.domain.model.JexxaEnum;
import io.jexxa.testapplication.domain.model.SpecialCasesValueObject;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

import static io.jexxa.drivingadapter.rest.JexxaWebProperties.*;
import static io.jexxa.drivingadapter.rest.RESTConstants.APPLICATION_TYPE;
import static io.jexxa.drivingadapter.rest.RESTConstants.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class OpenAPIIT
{
    private static final String OPENAPI_PATH = "http://localhost:7500/swagger-docs/";

    private RESTfulRPCAdapter objectUnderTest;


    public static Stream<Object> applicationServiceConfig() {
        return Stream.of(new SimpleApplicationService(), new Java8DateTimeApplicationService());
    }

    @BeforeEach
    void setupTests(){
        //Setup
        var properties = new Properties();
        var defaultHost = "localhost";
        var defaultPort = 7500;

        properties.put(JexxaWebProperties.JEXXA_REST_HOST, defaultHost);
        properties.put(JEXXA_REST_PORT, Integer.toString(defaultPort));
        properties.put(JEXXA_REST_OPEN_API_PATH, "swagger-docs");
        properties.put(JEXXA_REST_OPEN_API_SERVERS, "/test-url");

        objectUnderTest = RESTfulRPCAdapter.createAdapter(properties);
    }

    @AfterEach
    void tearDownTests(){
        //tear down
        objectUnderTest.stop();
        objectUnderTest = null;
        Unirest.shutDown();
    }


    @ParameterizedTest
    @MethodSource("applicationServiceConfig")
    void testBasicStructure(Object applicationService)
    {
        //Arrange
        objectUnderTest.register(applicationService);
        objectUnderTest.start();

        //Act
        JsonObject result = Unirest.get(OPENAPI_PATH)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(JsonObject.class).getBody();


        //Assert - Fields of basic openAPI structure
        assertNotNull(result);

        System.out.println(result);

        assertNotNull(result.get("openapi"));
        assertNotNull(result.get("info"));
        assertNotNull(result.get("paths"));
        assertNotNull(result.get("servers"));
    }


    @Test
    void testSpecialCasesValueObject()
    {
        //Arrange -> Nothing to do
        //Arrange
        objectUnderTest.register(new SimpleApplicationService());
        objectUnderTest.start();
        //Act
        JsonObject response = Unirest.get(OPENAPI_PATH)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(JsonObject.class).getBody();

        var result = response
                .get("components").getAsJsonObject()
                .get("schemas").getAsJsonObject()
                .get(SpecialCasesValueObject.class.getSimpleName()).getAsJsonObject();

        //Assert - Fields of basic openAPI structure
        assertNotNull(result);
        assertFalse(deepSearchKeys(result,"nullValue").isEmpty());
        assertFalse(deepSearchKeys(result,"valueWithoutGetter").isEmpty());
    }

    @Test
    void testEnum()
    {
        //Arrange -> Nothing to do
        //Arrange
        objectUnderTest.register(new SimpleApplicationService());
        objectUnderTest.start();
        //Act
        JsonObject response = Unirest.get(OPENAPI_PATH)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(JsonObject.class).getBody();

        var result = response
                .get("components").getAsJsonObject()
                .get("schemas").getAsJsonObject()
                .get(JexxaEnum.class.getSimpleName()).getAsJsonObject();

        //Assert - Fields of basic openAPI structure
        assertNotNull(result);
        assertTrue(result.toString().contains(JexxaEnum.ENUM_VALUE1.name()));
        assertTrue(result.toString().contains(JexxaEnum.ENUM_VALUE2.name()));
        assertTrue(result.toString().contains(JexxaEnum.ENUM_VALUE3.name()));
    }


    @ParameterizedTest
    @MethodSource("applicationServiceConfig")
    void testContentTypeIsJson(Object applicationService)
    {
        //Arrange
        objectUnderTest.register(applicationService);
        objectUnderTest.start();

        JsonObject openAPI = Unirest.get(OPENAPI_PATH)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(JsonObject.class).getBody();

        //Act
        var result = deepSearchKeys(openAPI, "content");

        //Assert - Fields of basic openAPI structure
        assertFalse( result.isEmpty() );
        result.forEach( element -> assertNotNull( element.getAsJsonObject().get(APPLICATION_TYPE) ) );
    }

    @ParameterizedTest
    @MethodSource("applicationServiceConfig")
    void testHTTPRequestMapping(Object applicationService)
    {
        //Arrange
        objectUnderTest.register(applicationService);
        objectUnderTest.start();

        var resTfulRPCConvention = new RESTfulRPCConvention(applicationService);

        JsonObject openAPI = Unirest.get(OPENAPI_PATH)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
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

    @ParameterizedTest
    @MethodSource("applicationServiceConfig")
    void testPostMethodDescription(Object applicationService)
    {
        //Arrange
        objectUnderTest.register(applicationService);
        objectUnderTest.start();

        JsonObject openAPI = Unirest.get(OPENAPI_PATH)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(JsonObject.class).getBody();

        var firstPostResult = deepSearchKeys(openAPI, "post").stream().findFirst().orElseThrow();

        //Act
        var operationID = deepSearchKeys(firstPostResult, "operationId").stream().findFirst().orElseThrow().getAsString();
        var description = deepSearchKeys(firstPostResult, "description").stream().findFirst().orElseThrow().getAsString();
        var summary = deepSearchKeys(firstPostResult, "summary").stream().findFirst().orElseThrow().getAsString();
        var tags = deepSearchKeys(firstPostResult, "tags").stream().findFirst().orElseThrow().getAsJsonArray();

        //Assert -- POST mapping
        assertFalse(operationID.isEmpty());
        assertEquals(operationID, description);
        assertEquals(operationID, summary);
        assertFalse(tags.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("applicationServiceConfig")
    void testGetMethodDescription(Object applicationService)
    {
        //Arrange
        objectUnderTest.register(applicationService);
        objectUnderTest.start();

        JsonObject openAPI = Unirest.get(OPENAPI_PATH)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(JsonObject.class).getBody();

        var firstPostResult = deepSearchKeys(openAPI, "get").stream().findFirst().orElseThrow();

        //Act
        var operationID = deepSearchKeys(firstPostResult, "operationId").stream().findFirst().orElseThrow().getAsString();
        var description = deepSearchKeys(firstPostResult, "description").stream().findFirst().orElseThrow().getAsString();
        var summary = deepSearchKeys(firstPostResult, "summary").stream().findFirst().orElseThrow().getAsString();
        var tags = deepSearchKeys(firstPostResult, "tags").stream().findFirst().orElseThrow().getAsJsonArray();

        //Assert -- POST mapping
        assertFalse(operationID.isEmpty());
        assertEquals(operationID, description);
        assertEquals(operationID, summary);
        assertFalse(tags.isEmpty());
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
