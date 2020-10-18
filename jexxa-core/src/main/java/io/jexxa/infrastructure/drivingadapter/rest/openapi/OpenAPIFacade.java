package io.jexxa.infrastructure.drivingadapter.rest.openapi;


import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter.OPEN_API_PATH;
import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCConvention.createRPCConvention;

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
        }
    }

    public void addOpenAPIDocumentation(Object object)
    {
        if ( openApiOptions != null )
        {
            var rpcConvention = createRPCConvention(object);
            rpcConvention
                    .getGETCommands()
                    .forEach(resTfulRPCMethod -> {
                        var openApiDocumentation = OpenApiBuilder
                                .document()
                                .operation(openApiOperation -> {
                                    openApiOperation.operationId(resTfulRPCMethod.getMethod().getName());
                                })
                                .json("200", resTfulRPCMethod.getMethod().getReturnType());
                        openApiOptions.setDocumentation(resTfulRPCMethod.getResourcePath(), HttpMethod.GET, openApiDocumentation);
                    });

            rpcConvention.getPOSTCommands().forEach(resTfulRPCMethod ->{
                var openApiDocumentation = OpenApiBuilder
                        .document()
                        .operation(openApiOperation -> {
                            openApiOperation.operationId(resTfulRPCMethod.getMethod().getName());
                        });

                if (resTfulRPCMethod.getMethod().getParameters().length == 1 )
                {
                    openApiDocumentation.body(resTfulRPCMethod.getMethod().getParameters()[0].getType());
                }  else if ( resTfulRPCMethod.getMethod().getParameters().length > 1 )
                {
                    //TODO: Implement for more than one argument
                }

                if ( resTfulRPCMethod.getMethod().getReturnType() != void.class )
                {
                    openApiDocumentation.json("200", resTfulRPCMethod.getMethod().getReturnType());
                }
                else {
                    openApiDocumentation.result("200");
                }


                openApiOptions.setDocumentation(resTfulRPCMethod.getResourcePath(), HttpMethod.POST, openApiDocumentation);

            });

            openApiOptions.defaultDocumentation(doc -> {
                doc.json("400", BadRequestResponse.class);
            });

        }
    }

}
