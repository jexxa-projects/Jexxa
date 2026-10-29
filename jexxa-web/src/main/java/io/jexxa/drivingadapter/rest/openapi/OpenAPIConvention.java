package io.jexxa.drivingadapter.rest.openapi;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;

import java.lang.reflect.*;
import java.net.HttpURLConnection;
import java.time.*;
import java.util.*;

import static io.jexxa.drivingadapter.rest.JexxaWebProperties.JEXXA_REST_OPEN_API_PATH;
import static io.jexxa.drivingadapter.rest.JexxaWebProperties.JEXXA_REST_OPEN_API_SERVERS;
import static io.jexxa.properties.JexxaCoreProperties.JEXXA_CONTEXT_NAME;
import static io.jexxa.properties.JexxaCoreProperties.JEXXA_CONTEXT_VERSION;

public class OpenAPIConvention {

    private static final String APPLICATION_TYPE_JSON = "application/json";

    private final OpenAPI openAPI;
    private final Properties properties;
    private final Components components = new Components();

    private final Set<Class<?>> registeredTypes = new HashSet<>();

    public OpenAPIConvention(Properties properties) {
        this.properties = Objects.requireNonNull(properties);

        openAPI = new OpenAPI()
                .openapi("3.0.1")
                .info(new Info()
                        .title(properties.getProperty(
                                JEXXA_CONTEXT_NAME, "Unknown Context"))
                        .description("Auto generated OpenAPI for "
                                + properties.getProperty(
                                JEXXA_CONTEXT_NAME, "Unknown Context"))
                        .version(properties.getProperty(
                                JEXXA_CONTEXT_VERSION, "1.0")))
                .servers(createServers())
                .paths(new Paths())
                .components(components);
    }

    public void documentGET(Method method, String resourcePath) {
        document(method, resourcePath, PathItem.HttpMethod.GET);
    }

    public void documentPOST(Method method, String resourcePath) {
        document(method, resourcePath, PathItem.HttpMethod.POST);
    }

    private void document(
            Method method,
            String resourcePath,
            PathItem.HttpMethod httpMethod) {

        if (isDisabled()) {
            return;
        }

        var operation = new Operation()
                .operationId(method.getName())
                .summary(method.getName())
                .description(method.getName())
                .addTagsItem(method.getDeclaringClass().getSimpleName())
                .responses(new ApiResponses()
                        .addApiResponse(
                                String.valueOf(HttpURLConnection.HTTP_OK),
                                createResponse(method))
                        .addApiResponse(
                                String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST),
                                new ApiResponse()
                                        .description("BAD REQUEST")
                                        .content(createContent(
                                                createSchema(BadRequestResponse.class)))));

        createRequestBody(method).ifPresent(operation::requestBody);

        var pathItem = openAPI.getPaths()
                .computeIfAbsent(resourcePath, ignored -> new PathItem());

        pathItem.operation(httpMethod, operation);
    }

    private ApiResponse createResponse(Method method) {

        var response = new ApiResponse().description("OK");

        if (method.getReturnType() != void.class) {
            response.content(createContent(
                    createSchema(method.getGenericReturnType())));
        }

        return response;
    }

    private Optional<RequestBody> createRequestBody(Method method) {

        var parameters = method.getGenericParameterTypes();

        if (parameters.length == 0) {
            return Optional.empty();
        }

        Schema<?> schema;

        if (parameters.length == 1) {
            schema = createSchema(parameters[0]);
        } else {
            var objectSchema = new ObjectSchema();

            for (int i = 0; i < parameters.length; i++) {
                objectSchema.addProperty(
                        "arg" + i,
                        createSchema(parameters[i]));
            }

            schema = objectSchema;
        }

        return Optional.of(
                new RequestBody()
                        .required(true)
                        .content(createContent(schema)));
    }

    private Content createContent(Schema<?> schema) {

        return new Content().addMediaType(
                APPLICATION_TYPE_JSON,
                new MediaType().schema(schema));
    }

    private Schema<?> createSchema(Type type) {

        if (type instanceof ParameterizedType parameterizedType) {

            var rawType = parameterizedType.getRawType();

            if (rawType instanceof Class<?> clazz) {

                if (Collection.class.isAssignableFrom(clazz)) {
                    return new ArraySchema().items(
                            createSchema(
                                    parameterizedType.getActualTypeArguments()[0]));
                }

                if (Optional.class.isAssignableFrom(clazz)) {
                    return createSchema(
                            parameterizedType.getActualTypeArguments()[0]);
                }

                if (Map.class.isAssignableFrom(clazz)) {
                    return new ObjectSchema().additionalProperties(
                            createSchema(
                                    parameterizedType.getActualTypeArguments()[1]));
                }

                return createSchema(clazz);
            }
        }

        if (type instanceof GenericArrayType arrayType) {
            return new ArraySchema().items(
                    createSchema(arrayType.getGenericComponentType()));
        }

        if (type instanceof WildcardType wildcardType) {
            return createSchema(wildcardType.getUpperBounds()[0]);
        }

        if (!(type instanceof Class<?> clazz)) {
            return new ObjectSchema();
        }

        if (clazz.isArray()) {
            return new ArraySchema().items(
                    createSchema(clazz.getComponentType()));
        }

        if (Collection.class.isAssignableFrom(clazz)) {
            return new ArraySchema();
        }

        if (clazz == String.class ||
                clazz == char.class ||
                clazz == Character.class) {
            return new StringSchema();
        }

        if (clazz == boolean.class || clazz == Boolean.class) {
            return new BooleanSchema();
        }

        if (clazz == int.class || clazz == Integer.class ||
                clazz == short.class || clazz == Short.class ||
                clazz == byte.class || clazz == Byte.class) {
            return new IntegerSchema();
        }

        if (clazz == long.class || clazz == Long.class) {
            return new IntegerSchema().format("int64");
        }

        if (clazz == float.class || clazz == Float.class) {
            return new NumberSchema().format("float");
        }

        if (clazz == double.class || clazz == Double.class ||
                Number.class.isAssignableFrom(clazz)) {
            return new NumberSchema().format("double");
        }

        if (clazz == LocalDate.class) {
            return new StringSchema().format("date");
        }

        if (clazz == LocalDateTime.class ||
                clazz == ZonedDateTime.class ||
                clazz == OffsetDateTime.class ||
                clazz == Instant.class) {
            return new StringSchema().format("date-time");
        }

        if (clazz.isEnum()) {
            registerEnum(clazz);

            return new Schema<>()
                    .$ref("#/components/schemas/" + clazz.getSimpleName());
        }

        registerComponent(clazz);

        return new Schema<>()
                .$ref("#/components/schemas/" + clazz.getSimpleName());
    }

    private void registerEnum(Class<?> clazz) {

        if (!registeredTypes.add(clazz)) {
            return;
        }

        var schema = new StringSchema();

        for (var constant : clazz.getEnumConstants()) {
            schema.addEnumItem(constant.toString());
        }

        components.addSchemas(clazz.getSimpleName(), schema);
    }
    private void registerComponent(Class<?> clazz) {

        if (!registeredTypes.add(clazz)) {
            return;
        }

        var schema = new ObjectSchema();

        // Register before resolving fields to support recursive types.
        components.addSchemas(clazz.getSimpleName(), schema);

        for (var field : clazz.getDeclaredFields()) {

            if (field.isSynthetic() ||
                    Modifier.isStatic(field.getModifiers()) ||
                    Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            schema.addProperty(
                    field.getName(),
                    createSchema(field.getGenericType()));
        }
    }

    private List<Server> createServers() {

        var servers = properties.getProperty(
                JEXXA_REST_OPEN_API_SERVERS, "");

        if (servers.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(servers.split(","))
                .map(String::trim)
                .filter(server -> !server.isEmpty())
                .map(server -> new Server().url(server))
                .toList();
    }

    public boolean isDisabled() {
        return getPath().isEmpty();
    }

    public Optional<String> getPath() {

        if (properties.containsKey(JEXXA_REST_OPEN_API_PATH)) {
            return Optional.of("/" +
                    properties.getProperty(JEXXA_REST_OPEN_API_PATH));
        }

        return Optional.empty();
    }

    public String getOpenAPI() {
        return Json.pretty(openAPI);
    }

    @SuppressWarnings("unused")
    private static class BadRequestResponse {
        public final String Exception = "";
        public final String ExceptionType = "";
        public final String ApplicationType = APPLICATION_TYPE_JSON;
    }
}