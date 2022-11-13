package io.jexxa.infrastructure.drivingadapter.rest.openapi;


import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import static io.jexxa.infrastructure.drivingadapter.rest.JexxaWebProperties.JEXXA_REST_OPEN_API_PATH;
import static io.jexxa.utils.properties.JexxaCoreProperties.JEXXA_CONTEXT_NAME;
import static io.jexxa.utils.properties.JexxaCoreProperties.JEXXA_CONTEXT_VERSION;
import static org.eclipse.microprofile.openapi.OASFactory.createAPIResponse;
import static org.eclipse.microprofile.openapi.OASFactory.createAPIResponses;
import static org.eclipse.microprofile.openapi.OASFactory.createComponents;
import static org.eclipse.microprofile.openapi.OASFactory.createContent;
import static org.eclipse.microprofile.openapi.OASFactory.createInfo;
import static org.eclipse.microprofile.openapi.OASFactory.createMediaType;
import static org.eclipse.microprofile.openapi.OASFactory.createOpenAPI;
import static org.eclipse.microprofile.openapi.OASFactory.createOperation;
import static org.eclipse.microprofile.openapi.OASFactory.createPathItem;
import static org.eclipse.microprofile.openapi.OASFactory.createPaths;
import static org.eclipse.microprofile.openapi.OASFactory.createSchema;

@SuppressWarnings("java:S1602") // required to avoid ambiguous warnings
public class OpenAPIConvention
{
    private static final String APPLICATION_TYPE_JSON = "application/json";

    private final OpenAPI openAPI;
    private final Properties properties;

    private final Components components;

    public OpenAPIConvention(Properties properties)
    {
        this.properties = properties;
        components = createComponents();


        openAPI = createOpenAPI()
                .openapi("3.0.1")
                .info(
                        createInfo()
                                .title(properties.getProperty(JEXXA_CONTEXT_NAME, "Unknown Context"))
                                .description("Auto generated OpenAPI for " + properties.getProperty(JEXXA_CONTEXT_NAME, "Unknown Context"))
                                .version(properties.getProperty(JEXXA_CONTEXT_VERSION, "1.0"))
                )
                .paths(
                        createPaths()
                ).components(components);
    }
    public void documentGET(Method method, String resourcePath)
    {
        if (isDisabled())
        {
            return;
        }
        openAPI.getPaths().addPathItem(
            resourcePath, createPathItem()
                        .GET(createOperation()
                                .operationId(method.getName())
                                .responses(
                                        createAPIResponses()
                                                .addAPIResponse(
                                                        "200", documentReturnType(method)
                                                    )
                                        //TODO: Add BadRequest
                                        //TODO: Add Parameters
                                    )
                    )
        );
    }

    public void documentPOST(Method method, String resourcePath)
    {
        if (isDisabled())
        {
            return;
        }

        openAPI.getPaths().addPathItem(
                resourcePath, createPathItem()
                        .POST(createOperation()
                                .operationId(method.getName())
                                .responses(
                                        createAPIResponses()
                                                .addAPIResponse("200", documentReturnType(method))
                                        //TODO: Add BadRequest
                                        //TODO: Add Parameters
                                )
                        )
        );
    }
    public boolean isDisabled()
    {
        return getPath().isEmpty();
    }

    private APIResponse documentReturnType(Method method)
    {
        if (method.getReturnType().equals(void.class))
        {
            return createAPIResponse().description("OK");
        }

        var mediaType = createMediaType()
                .schema(createInternalSchema(method.getReturnType(), method.getGenericReturnType()));

        var apiResponse = createAPIResponse()
                .description("OK")
                .content(createContent().addMediaType(APPLICATION_TYPE_JSON, mediaType));


        if ( isJsonArray(method.getReturnType()) )
        {
            addComponent(extractTypeFromArray(method.getGenericReturnType()));
        } else  {
            addComponent(method.getReturnType());
        }

        return apiResponse;
    }

    public String getOpenAPI() {
        try {
            return OpenApiSerializer.serialize(openAPI, Format.JSON);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<String> getPath()
    {
        if (properties.containsKey(JEXXA_REST_OPEN_API_PATH)) {
            return Optional.of("/" + properties.getProperty(JEXXA_REST_OPEN_API_PATH));
        }
        return Optional.empty();
    }


  /*  private static final String APPLICATION_TYPE_JSON = "application/json";
    private static final String JSON_OBJECT_TYPE = "object";
    private static final String JSON_ARRAY_TYPE = "array";

    private final Properties properties;
    private final JavalinConfig javalinConfig;
    private OpenApiOptions openApiOptions;

    public OpenAPIConvention(Properties properties, JavalinConfig javalinConfig)
    {
        this.properties = properties;
        this.javalinConfig = javalinConfig;

        initOpenAPI();
    }
    private void initOpenAPI()
    {
        if (properties.containsKey(JEXXA_REST_OPEN_API_PATH))
        {
            OpenApiVersionUtil.INSTANCE.setLogWarnings(false);

            var applicationInfo = new Info()
                    .version(properties.getProperty(JEXXA_CONTEXT_VERSION, "1.0"))
                    .description("Auto generated OpenAPI for " + properties.getProperty(JEXXA_CONTEXT_NAME, "Unknown Context"))
                    .title(properties.getProperty(JEXXA_CONTEXT_NAME, "Unknown Context"));

            openApiOptions = new OpenApiOptions(applicationInfo)
                    .path("/" + properties.getProperty(JEXXA_REST_OPEN_API_PATH));

            //Show all fields of an ValueObject
            openApiOptions.getJacksonMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            javalinConfig.registerPlugin(new OpenApiPlugin(openApiOptions));
            javalinConfig.enableCorsForAllOrigins();

            openApiOptions.defaultDocumentation(doc -> {
                doc.json(String.valueOf(HTTP_BAD_REQUEST), BadRequestResponse.class);
                doc.json(String.valueOf(HTTP_BAD_REQUEST), BadRequestResponse.class);
            });
        }
    }

    boolean isEnabled()
    {
        return openApiOptions != null;
    }

    public Optional<String> getPath()
    {
        if (isEnabled()) {
            return Optional.of("/" + properties.getProperty(JEXXA_REST_OPEN_API_PATH));
        }

        return Optional.empty();
    }

    public void documentGET(Method method, String resourcePath)
    {
        if ( openApiOptions == null )
        {
            return;
        }

        var openApiDocumentation = OpenApiBuilder
                .document()
                .operation(openApiOperation -> {
                    openApiOperation.operationId(method.getName());
                });

        documentReturnType(method, openApiDocumentation);

        openApiOptions.setDocumentation(resourcePath, HttpMethod.GET, openApiDocumentation);
    }

    public void documentPOST(Method method, String resourcePath)
    {
        if ( openApiOptions == null )
        {
            return;
        }

        var openApiDocumentation = OpenApiBuilder
                .document()
                .operation(openApiOperation -> {
                    openApiOperation.operationId(method.getName());
                });

        documentParameters(method, openApiDocumentation);

        documentReturnType(method, openApiDocumentation);

        openApiOptions.setDocumentation(resourcePath, HttpMethod.POST, openApiDocumentation);
    }

    private static void documentReturnType(Method method, OpenApiDocumentation openApiDocumentation)
    {
        if ( isJsonArray(method.getReturnType()) )
        {
            openApiDocumentation.jsonArray("200", extractTypeFromArray(method.getGenericReturnType()));
        } else if ( method.getReturnType() != void.class )
        {
            openApiDocumentation.json("200", method.getReturnType());
        }
        else {
            openApiDocumentation.result("200");
        }
    }

    private static void documentParameters(Method method, OpenApiDocumentation openApiDocumentation)
    {
        if (method.getParameters().length == 1 )
        {
            var schema = createSchema(method.getParameterTypes()[0], method.getGenericParameterTypes()[0]);
            schema.setExample(createExampleInstance(method.getParameterTypes()[0], method.getGenericParameterTypes()[0]));

            //For some reason I have to add requests as DocumentedContent. Otherwise, components seems to be not correctly created
            openApiDocumentation.body(List.of(new DocumentedContent(method.getParameterTypes()[0], isJsonArray(method.getParameterTypes()[0]), APPLICATION_TYPE_JSON )));
            openApiDocumentation.body(schema, APPLICATION_TYPE_JSON);
        }  else if ( method.getParameters().length > 1 )
        {
            var schema = new ComposedSchema();
            var exampleObjects = new Object[method.getParameterTypes().length];
            var documentedContend = new ArrayList<DocumentedContent>();

            for (var i = 0; i < method.getParameterTypes().length; ++i)
            {
                exampleObjects[i] = createExampleInstance(method.getParameterTypes()[i],method.getGenericParameterTypes()[i]);
                var parameterSchema = createSchema(method.getParameterTypes()[i], method.getGenericParameterTypes()[i]);
                parameterSchema.setExample(exampleObjects[i]);

                documentedContend.add( new DocumentedContent(method.getParameterTypes()[i], isJsonArray(method.getParameterTypes()[i]), APPLICATION_TYPE_JSON ));
                schema.addAnyOfItem(parameterSchema);
            }

            schema.setExample(exampleObjects);
            //For some reason I have to add requests as DocumentedContent. Otherwise, components seems to be not correctly created
            openApiDocumentation.body(documentedContend);
            openApiDocumentation.body(schema, APPLICATION_TYPE_JSON);
        }
    }*/

    /*private static String createJsonSchema(Class<?> clazz) throws JsonProcessingException
    {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());


        //There are other configuration options you can set.  This is the one I needed.
        mapper.configure(WRITE_ENUMS_USING_TO_STRING, true);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // StdDateFormat is ISO8601 since jackson 2.9
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        var jsonSchemaGenerator = new JsonSchemaGenerator(mapper);
        var jsonSchema = jsonSchemaGenerator.generateJsonSchema(clazz);

        return mapper.writeValueAsString(jsonSchema);
    }*/


    private static Schema createInternalSchema(Class<?> clazz, Type genericType)
    {
        var schema = createSchema();

        if ( isInteger(clazz) )
        {
            schema.setType(Schema.SchemaType.INTEGER);
            return schema;
        }

        if ( isNumber(clazz) )
        {
            schema.setType(Schema.SchemaType.NUMBER);
            return schema;
        }

        if ( isBoolean(clazz) )
        {
            schema.setType(Schema.SchemaType.BOOLEAN);
            return schema;
        }

        if ( isString(clazz) || isJava8Date(clazz))
        {
            schema.setType(Schema.SchemaType.STRING);
            return schema;
        }

        if ( isJsonArray(clazz) )
        {
            schema.setType(Schema.SchemaType.ARRAY);
            schema.setItems(createInternalSchema(extractTypeFromArray(genericType), null));
            return schema;
        }

        schema.setType(Schema.SchemaType.OBJECT);
        schema.setRef(clazz.getSimpleName());
        return schema;
    }

    private void addComponent(Class<?> clazz)
    {
        // do not add build in data types as components
        if (isBoolean(clazz) || isInteger(clazz) || isNumber(clazz) || isString(clazz) || isJava8Date(clazz))
        {
            return;
        }

        if (components.getSchemas() == null || !components.getSchemas().containsKey(clazz.getSimpleName()))
        {
            var schema = createSchema();
            schema.setType(Schema.SchemaType.OBJECT);
            Stream.of(clazz.getDeclaredFields())
                    .filter(element -> !Modifier.isStatic(element.getModifiers()))
                    .forEach(element -> schema.addProperty(element.getName(), createComponentSchema(element.getType(), element.getGenericType())));
            components.addSchema(clazz.getSimpleName(), schema);
        }
    }

    private Schema createComponentSchema(Class<?> clazz, Type genericType)
        {
            var schema = createSchema();

            if ( isInteger(clazz) )
            {
                schema.setType(Schema.SchemaType.INTEGER);
                return schema;
            }

            if ( isNumber(clazz) )
            {
                schema.setType(Schema.SchemaType.NUMBER);
                return schema;
            }

            if ( isBoolean(clazz) )
            {
                schema.setType(Schema.SchemaType.BOOLEAN);
                return schema;
            }

            if ( isString(clazz) || isJava8Date(clazz))
            {
                schema.setType(Schema.SchemaType.STRING);
                return schema;
            }

            if ( isJsonArray(clazz) )
            {
                schema.setType(Schema.SchemaType.ARRAY);
                //TODO: schema.setRef(extractTypeFromArray(genericType).getSimpleName());
                // TODO Handle arrays
                return schema;
            }

            schema.setType(Schema.SchemaType.OBJECT);
            Stream.of(clazz.getDeclaredFields())
                .filter(element -> !Modifier.isStatic(element.getModifiers()))
                .forEach(element -> schema.addProperty(element.getName(), createComponentSchema(element.getType(), element.getGenericType())));

            return schema;
        }


    private static boolean isInteger(Class<?> clazz)
    {
        return clazz.equals(Short.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(short.class) ||
                clazz.equals(int.class);
    }

    private static boolean isNumber(Class<?> clazz)
    {
        return Number.class.isAssignableFrom(clazz) ||
                clazz.equals(byte.class) ||
                clazz.equals(long.class) ||
                clazz.equals(float.class) ||
                clazz.equals(double.class);
    }

    private static boolean isJava8Date(Class<?> clazz)
    {
        return clazz.equals( LocalDate.class ) ||
                clazz.equals(LocalDateTime.class) ||
                clazz.equals(ZonedDateTime.class);
    }

    private static boolean isBoolean(Class<?> clazz)
    {
        return clazz.equals( Boolean.class ) ||
                clazz.equals( boolean.class );
    }

    private static boolean isString(Class<?> clazz)
    {
        return clazz.equals( String.class ) ||
                clazz.equals( char.class );
    }

    private static boolean isJsonArray(Class<?> clazz)
    {
        return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
    }

    private static Class<?> extractTypeFromArray(Type type)
    {
        var parameterType = (ParameterizedType) type;
        return (Class<?>)parameterType.getActualTypeArguments()[0];
    }

    @SuppressWarnings({"java:S1104", "java:S116", "java:S1170","unused"})
    private static class BadRequestResponse
    {
        public final String Exception = "";
        public final String ExceptionType = "";
        public final String ApplicationType = APPLICATION_TYPE_JSON;
        BadRequestResponse()
        {
            //private constructor
        }
    }
}
