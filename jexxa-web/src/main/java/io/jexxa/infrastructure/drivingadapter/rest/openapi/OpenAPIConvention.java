package io.jexxa.infrastructure.drivingadapter.rest.openapi;


import io.smallrye.openapi.api.models.parameters.RequestBodyImpl;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
        var operation = createOperation()
                .operationId(method.getName())
                .responses(
                        createAPIResponses()
                                .addAPIResponse(String.valueOf(HttpURLConnection.HTTP_OK), documentReturnType(method))
                                .addAPIResponse(String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST), documentException())
                );

        createRequestBody(method).ifPresent(operation::requestBody);

        openAPI.getPaths().addPathItem(
            resourcePath, createPathItem().GET(operation)
        );
    }


    public void documentPOST(Method method, String resourcePath)
    {
        if (isDisabled())
        {
            return;
        }
        var operation = createOperation()
                .operationId(method.getName())
                .responses(
                        createAPIResponses()
                                .addAPIResponse(String.valueOf(HttpURLConnection.HTTP_OK), documentReturnType(method))
                                .addAPIResponse(String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST), documentException())
                );


        createRequestBody(method).ifPresent(operation::requestBody);

        openAPI.getPaths().addPathItem(
                resourcePath, createPathItem().POST(operation)
        );
    }

    private Optional<RequestBody> createRequestBody(Method method) {
        if (method.getParameters().length == 0)
        {
            return Optional.empty();
        }

        var requestBody = new RequestBodyImpl();

        if (method.getParameterCount()  == 1 )
        {
            requestBody.setRequired(true);
            var mediaType = createMediaType().schema(createInternalSchema(method.getParameterTypes()[0], method.getGenericParameterTypes()[0]));
            mediaType.setExample(createExample(method.getParameterTypes()[0], method.getGenericParameterTypes()[0]));
            requestBody.content(createContent().addMediaType(APPLICATION_TYPE_JSON, mediaType));
            addComponent(method.getParameterTypes()[0]);

        }  else if ( method.getParameterCount() > 1 )
        {
            requestBody.setRequired(true);
            var schema = createSchema();
            var example = new ArrayList<>(method.getParameterCount());
            for (var i = 0; i < method.getParameterTypes().length; ++i)
            {
                schema.addAnyOf(createInternalSchema(method.getParameterTypes()[i], method.getGenericParameterTypes()[i]));
                example.add( createExample(method.getParameterTypes()[i], method.getGenericParameterTypes()[i]));
                addComponent(method.getParameterTypes()[i]);
            }
            var mediaType = createMediaType().schema(schema);
            mediaType.setExample(example);
            requestBody.content(createContent().addMediaType(APPLICATION_TYPE_JSON, mediaType));
        }
        return Optional.of(requestBody);
    }

    private APIResponse documentException()
    {
        addComponent(BadRequestResponse.class);

        var mediaType = createMediaType()
                .schema(createInternalSchema(BadRequestResponse.class, BadRequestResponse.class));

        return createAPIResponse()
                .description("BAD REQUEST")
                .content(createContent().addMediaType(APPLICATION_TYPE_JSON, mediaType));
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
            // TODO Handle arrays
            return schema;
        }

        schema.setType(Schema.SchemaType.OBJECT);
        Stream.of(clazz.getDeclaredFields())
            .filter(element -> !Modifier.isStatic(element.getModifiers()))
            .forEach(element -> schema.addProperty(element.getName(), createComponentSchema(element.getType(), element.getGenericType())));

        return schema;
    }

    private Object createExample(Class<?> clazz, Type genericType)
    {
        if ( isInteger(clazz) || isNumber(clazz))
        {
            return 0;
        }

        if ( isBoolean(clazz) )
        {
            return true;
        }

        if ( isString(clazz))
        {
            return "";
        }
        if ( isJava8Date(clazz))
        {
            return "2022-11-13T06:08:41Z";
        }

        if ( isJsonArray(clazz) )
        {
            var returnValue = new Object[1];
            returnValue[0]= createExample(extractTypeFromArray(genericType), genericType);
            return returnValue;
        }

        return null;
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
