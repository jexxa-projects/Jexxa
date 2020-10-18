package io.jexxa.infrastructure.drivingadapter.rest.openapi;


import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter.OPEN_API_PATH;

import java.lang.reflect.Method;
import java.util.Properties;

import io.javalin.core.JavalinConfig;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import io.swagger.v3.oas.models.info.Info;

public class OpenAPIFacade
{
    private final Properties properties;
    private final JavalinConfig javalinConfig;
    private OpenApiOptions openApiOptions;

    public OpenAPIFacade(Properties properties, JavalinConfig javalinConfig)
    {
        this.properties = properties;
        this.javalinConfig = javalinConfig;
        initOpenAPI();
    }
    private void initOpenAPI()
    {
        if (properties.containsKey(OPEN_API_PATH))
        {
            Info applicationInfo = new Info()
                    .version("1.0")
                    .description(properties.getProperty("io.jexxa.context.name", "Unknown Context"))
                    .title(properties.getProperty("io.jexxa.context.name", "Unknown Context"));

            openApiOptions = new OpenApiOptions(applicationInfo)
                    .path("/" + properties.getProperty(OPEN_API_PATH));

            javalinConfig.registerPlugin(new OpenApiPlugin(openApiOptions));
            javalinConfig.enableCorsForAllOrigins();

            openApiOptions.defaultDocumentation(doc -> {
                doc.json("400", BadRequestResponse.class);
            });

        }
    }

    public void documentGET(Method method, String resourcePath)
    {
        if ( openApiOptions != null )
        {
            var openApiDocumentation = OpenApiBuilder
                    .document()
                    .operation(openApiOperation -> {
                        openApiOperation.operationId(method.getName());
                    })
                    .json("200", method.getReturnType());
            openApiOptions.setDocumentation(resourcePath, HttpMethod.GET, openApiDocumentation);
        }
    }

    public void documentPOST(Method method, String resourcePath)
    {
        if ( openApiOptions != null )
        {
            var openApiDocumentation = OpenApiBuilder
                    .document()
                    .operation(openApiOperation -> {
                        openApiOperation.operationId(method.getName());
                    });

            if (method.getParameters().length == 1 )
            {
                openApiDocumentation.body(method.getParameters()[0].getType());
            }  else if ( method.getParameters().length > 1 )
            {
                //TODO: Implement for more than one argument
            }

            if ( method.getReturnType() != void.class )
            {
                openApiDocumentation.json("200", method.getReturnType());
            }
            else {
                openApiDocumentation.result("200");
            }


            openApiOptions.setDocumentation(resourcePath, HttpMethod.POST, openApiDocumentation);
        }
    }
}
