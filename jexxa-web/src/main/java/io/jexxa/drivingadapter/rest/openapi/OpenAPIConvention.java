package io.jexxa.drivingadapter.rest.openapi;


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
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import static io.jexxa.common.JexxaCoreProperties.JEXXA_CONTEXT_NAME;
import static io.jexxa.common.JexxaCoreProperties.JEXXA_CONTEXT_VERSION;
import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;
import static io.jexxa.drivingadapter.rest.JexxaWebProperties.JEXXA_REST_OPEN_API_PATH;
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
        try {
            var operation = createOperation()
                    .operationId(method.getName())
                    .responses(
                            createAPIResponses()
                                    .addAPIResponse(String.valueOf(HttpURLConnection.HTTP_OK), createAPIResponseForReturnType(method))
                                    .addAPIResponse(String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST), createAPIResponseForException())
                    )
                    .description(method.getName())
                    .summary(method.getName())
                    .addTag(method.getDeclaringClass().getSimpleName());

            createRequestBody(method).ifPresent(operation::requestBody);

            openAPI.getPaths().addPathItem(
                    resourcePath, createPathItem().GET(operation)
            );

            addComponents(method);
        } catch (ClassCastException e)
        {
                getLogger(OpenAPIConvention.class).error("Could not generate OpenAPI for method {}::{}. Please check attributes and return type.", method.getDeclaringClass().getName(),  method.getName());
        }

    }


    public void documentPOST(Method method, String resourcePath)
    {
        if (isDisabled())
        {
            return;
        }
        try {
            var operation = createOperation()
                    .operationId(method.getName())
                    .responses(
                            createAPIResponses()
                                    .addAPIResponse(String.valueOf(HttpURLConnection.HTTP_OK), createAPIResponseForReturnType(method))
                                    .addAPIResponse(String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST), createAPIResponseForException())
                    )
                    .description(method.getName())
                    .summary(method.getName())
                    .addTag(method.getDeclaringClass().getSimpleName());

            createRequestBody(method).ifPresent(operation::requestBody);

            openAPI.getPaths().addPathItem(
                    resourcePath, createPathItem().POST(operation)
            );

            addComponents(method);
        } catch (ClassCastException e)
        {
            getLogger(OpenAPIConvention.class).error("Could not generate OpenAPI for method {}::{}. Please check attributes and return type.", method.getDeclaringClass().getName(),  method.getName());
        }
    }

    public boolean isDisabled()
    {
        return getPath().isEmpty();
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

    private void addComponents(Method method)
    {
        Stream.of(method.getParameters()).map(Parameter::getType).forEach(this::addComponent);

        if ( isCollection(method.getReturnType()) )
        {
            addComponent(extractTypeFromCollection(method.getReturnType(), method.getGenericReturnType()));
        } else  {
            addComponent(method.getReturnType());
        }
        addComponent(BadRequestResponse.class);
    }

    private Optional<RequestBody> createRequestBody(Method method) {
        if (method.getParameters().length == 0)
        {
            return Optional.empty();
        }

        var requestBody = new RequestBodyImpl();
        requestBody.setRequired(true);

        if (method.getParameterCount()  == 1 )
        {
            var mediaType = createMediaType()
                    .schema(createReferenceSchema(method.getParameterTypes()[0], method.getGenericParameterTypes()[0]))
                    .example(createExample(method.getParameterTypes()[0], method.getGenericParameterTypes()[0]));

            requestBody.content(createContent().addMediaType(APPLICATION_TYPE_JSON, mediaType));
        }

        if ( method.getParameterCount() > 1 )
        {
            var mediaType = createMediaType();

            var schema = createSchema();
            for (var i = 0; i < method.getParameterTypes().length; ++i) {
                var argSchema = createReferenceSchema(method.getParameterTypes()[i], method.getGenericParameterTypes()[i]);
                argSchema.setExample(createExample(method.getParameterTypes()[i], method.getGenericParameterTypes()[i]));
                schema.addProperty("arg"+ i, argSchema);
            }
            mediaType.setSchema(schema);
            requestBody.content(createContent().addMediaType(APPLICATION_TYPE_JSON, mediaType));
        }

        return Optional.of(requestBody);
    }


    private APIResponse createAPIResponseForException()
    {
        return createAPIResponse()
                .description("BAD REQUEST")
                .content(createContent().addMediaType(APPLICATION_TYPE_JSON,
                        createMediaType()
                                .schema(createReferenceSchema(BadRequestResponse.class, BadRequestResponse.class))
                ));
    }


    private APIResponse createAPIResponseForReturnType(Method method)
    {
        if (method.getReturnType().equals(void.class))
        {
            return createAPIResponse().description("OK");
        }

        return createAPIResponse()
                .description("OK")
                .content(createContent().addMediaType(APPLICATION_TYPE_JSON,
                        createMediaType()
                                .schema(createReferenceSchema(method.getReturnType(), method.getGenericReturnType()))
                ));
    }


    private static Schema createReferenceSchema(Class<?> clazz, Type genericType)
    {
        if (isBaseType(clazz))
        {
            return createSchemaBaseType(clazz);
        }

        if ( isCollection(clazz) )
        {
            if (genericType != null ) {
                return createSchema()
                        .type(Schema.SchemaType.ARRAY)
                        .items(createReferenceSchema(extractTypeFromCollection(clazz, genericType), null));
            }
            return createSchema().type(Schema.SchemaType.ARRAY);
        }

        return createSchema()
                .type(Schema.SchemaType.OBJECT)
                .ref(clazz.getSimpleName());
    }

    private void addComponent(Class<?> clazz)
    {
        // do not add build in data types as components
        if (isBaseType(clazz) || clazz.equals(void.class))
        {
            return;
        }

        if (components.getSchemas() == null || !components.getSchemas().containsKey(clazz.getSimpleName()))
        {
            if (clazz.isEnum())
            {
                components.addSchema(clazz.getSimpleName(), createSchemaEnum(clazz));
            } else {
                var schema = createSchema();
                schema.setType(Schema.SchemaType.OBJECT);
                Stream.of(clazz.getDeclaredFields())
                        .filter(element -> !Modifier.isStatic(element.getModifiers()))
                        .forEach(element -> schema.addProperty(element.getName(), createComponentSchema(element.getType(), element.getGenericType())));
                components.addSchema(clazz.getSimpleName(), schema);
            }
        }
    }

    private Schema createComponentSchema(Class<?> clazz, Type genericType)
    {
        if (isBaseType(clazz))
        {
            return createSchemaBaseType(clazz);
        }

        if ( isCollection(clazz) )
        {
            if (genericType != null) {
                createComponentSchema(extractTypeFromCollection(clazz, genericType), null);
                var extractedType = extractTypeFromCollection(clazz, genericType);
                if (isBaseType(extractedType)) {
                    return createSchema().type(Schema.SchemaType.ARRAY).items(createSchemaBaseType(extractedType));
                } else {
                    return createSchema().type(Schema.SchemaType.ARRAY).ref(extractedType.getSimpleName());
                }
            }
            return createSchema().type(Schema.SchemaType.ARRAY);
        }

        var schema = createSchema();
        schema.setType(Schema.SchemaType.OBJECT);
        Stream.of(clazz.getDeclaredFields())
            .filter(element -> !Modifier.isStatic(element.getModifiers()))
            .forEach(element -> schema.addProperty(element.getName(),
                    createComponentSchema(element.getType(), element.getGenericType())
            ));

        return schema;
    }

    private Schema createSchemaEnum(Class<?> clazz) {
        var enumSchema = createSchema().type(Schema.SchemaType.STRING);
        Stream.of(clazz.getEnumConstants()).forEach( element -> enumSchema.addEnumeration(element.toString()));
        return enumSchema;
    }

    private static Schema createSchemaBaseType(Class<?> clazz)
    {
        if ( isInteger(clazz) )
        {
            return createSchema().type(Schema.SchemaType.INTEGER);
        }

        if ( isNumber(clazz) )
        {
            return createSchema().type(Schema.SchemaType.NUMBER);
        }

        if ( isBoolean(clazz) )
        {
            return createSchema().type(Schema.SchemaType.BOOLEAN);
        }

        if ( isString(clazz) || isJava8Date(clazz))
        {
            return createSchema().type(Schema.SchemaType.STRING);
        }

        throw new IllegalArgumentException("Given Class " + clazz.getSimpleName() + " is not a base type");
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

        if (clazz.isEnum())
        {
            if (clazz.getEnumConstants().length >0)
            {
                return clazz.getEnumConstants()[0].toString();
            }

            return null;
        }

        if ( isCollection(clazz) && genericType != null)
        {
            var returnValue = new Object[1];
            returnValue[0] = createExample(extractTypeFromCollection(clazz, genericType), genericType);
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

    private static boolean isBaseType(Class<?> clazz)
    {
        return isBoolean(clazz) || isString(clazz) || isNumber(clazz) || isJava8Date(clazz) || isInteger(clazz);
    }

    private static boolean isCollection(Class<?> clazz)
    {
        return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
    }

    private static Class<?> extractTypeFromCollection(Class<?> clazz, Type type)
    {
       if (clazz.isArray()) {
            return clazz.getComponentType();
        } else {
           var parameterType = (ParameterizedType) type;
           return (Class<?>) parameterType.getActualTypeArguments()[0];
        }
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
