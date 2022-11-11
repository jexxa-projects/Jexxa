package io.jexxa.infrastructure.drivingadapter.rest.openapi;


@SuppressWarnings("java:S1602") // required to avoid ambiguous warnings
public class OpenAPIConvention
{
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
    }

    private static String createJsonSchema(Class<?> clazz) throws JsonProcessingException
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
    }

    private static Object createExampleInstance(Class<?> clazz, Type genericType)
    {
        if ( isJava8Date(clazz) )
        {
            return java8DateExample(clazz);
        }

        return createGenericExample(clazz, genericType);
    }

    private static Object java8DateExample(Class<?> clazz)
    {
        if ( clazz.equals( LocalDate.class ) )
        {
            return LocalDate.of(1970, 1, 1).toString();
        }

        if ( clazz.equals( LocalDateTime.class ) )
        {
            return LocalDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.of(0,0,0) ).toString();
        }

        if (  clazz.equals(ZonedDateTime.class) )
        {
            return ZonedDateTime.of(1970,1,1,0,0,0,0, ZoneId.systemDefault()).withFixedOffsetZone().toString();
        }

        return null;
    }

    private static Object createGenericExample(Class<?> clazz, Type genericType)
    {
        try
        {
            // We create a JsonSchema and try to create an instance of given class from it.
            // Motivation is that if we cannot create an Object via class -> JsonSchema -> Object it is most unlikely that we can handle attribute in some meaningful way
            var schemaString = createJsonSchema(clazz);
            var jsonObject = JsonParser.parseString(schemaString).getAsJsonObject();
            if (jsonObject == null || jsonObject.get("type") == null )
            {
                JexxaLogger.getLogger(OpenAPIConvention.class).warn("Could not create Json schema for given class `{}`", clazz.getName());
                return null;
            }

            var typeInformation = jsonObject.get("type");

            //Handle JsonObject
            if (typeInformation.getAsString().equals(JSON_OBJECT_TYPE))
            {
                if (Modifier.isAbstract( clazz.getModifiers()) || Modifier.isInterface( clazz.getModifiers() ) )
                {
                    JexxaLogger.getLogger(OpenAPIConvention.class).warn("Given class `{}` is abstract or an interface => Can not create an example object for OpenAPI", clazz.getName());
                    return null;
                }

                return JSONManager.getJSONConverter().fromJson(jsonObject.toString(), clazz);
            }

            //Handle JsonArray
            if (typeInformation.getAsString().equals(JSON_ARRAY_TYPE))
            {
                var result = new Object[1];
                result[0] = createExampleInstance(extractTypeFromArray(genericType), null);
                return result;
            }

            //Handle primitive values
            return createPrimitive(clazz);
        } catch (Exception | NoSuchMethodError e ) {
            if (!clazz.isRecord()) { // Records include some isolation fields that can not be created and are not meaningful for a template. Therefore, we show this message only given type is not a record
                JexxaLogger.getLogger(OpenAPIConvention.class).warn("[OpenAPI] Could not create an example Object {}", clazz.getName());
            }
        }
        return null;
    }


    private static Object createPrimitive(Class<?> clazz)
    {
        if (isInteger(clazz) || isNumber(clazz))
        {
            return 1;
        }

        if (isBoolean( clazz))
        {
            return true;
        }

        if (isString(clazz))
        {
            return "string";
        }

        return null;
    }

    private static Schema<?> createSchema(Class<?> clazz, Type genericType)
    {
        if ( isInteger(clazz) )
        {
            return new IntegerSchema();
        }

        if ( isNumber(clazz) )
        {
            return new NumberSchema();
        }

        if ( isBoolean(clazz) )
        {
            return new BooleanSchema();
        }

        if ( isString(clazz) )
        {
            return new StringSchema();
        }

        if ( isJava8Date(clazz) )
        {
            return new StringSchema();
        }

        if ( isJsonArray(clazz) )
        {
            var schema = new ArraySchema();
            schema.setItems(createSchema(extractTypeFromArray(genericType), null));
            return schema;
        }

        var result = new ObjectSchema();

        result.set$ref(clazz.getSimpleName());

        return result;
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
    }*/
}
