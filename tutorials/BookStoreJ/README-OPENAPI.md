# BookStoreJ - With OpenAPI support 

## What You Learn

*   How to enable OpenAPI support for an application 
*   How to access application from outside using OpenAPI        

## What you need

*   Understand tutorial `BookStoreJ` because we explain only new aspects 
*   30 minutes
*   JDK 11 (or higher) installed
*   [Swagger UI](https://swagger.io/tools/swagger-ui/)    
*   Maven 3.6 (or higher) installed
*   A postgres DB (if you start the application with option `-jdbc')  

## Run the application  

In general OpenAPI support can be enabled for all objects that can be accessed via the `RESTfulRPCAdapter`. 
To enable OpenAPI support, you just need to define an OpenAPI-path either in the properties or when starting the application. 
The corresponding parameter is `io.jexxa.rest.open_api_path`.

### Enable OpenAPI via console

```console                                                          
mvn clean install
java -Dio.jexxa.rest.open_api_path=swagger-docs -jar target/bookstorej-jar-with-dependencies.jar 
```
You will see following (or similar) output
```console
...
[main] INFO io.javalin.Javalin - Listening on http://0.0.0.0:7000/
[main] INFO io.javalin.Javalin - Javalin started in 188ms \o/
[main] INFO io.javalin.Javalin - OpenAPI documentation available at: http://0.0.0.0:7000/swagger-docs
[main] INFO io.jexxa.core.JexxaMain - BoundedContext 'BookStoreJApplication' successfully started in 1.299 seconds

```          

### Explore OpenAPI

You can use [Swagger UI](https://swagger.io/tools/swagger-ui/) to explore the documentation. Just start Swagger UI, enter the URL and press explore button. 

Note: In case you start Swagger UI not on the same machine as your BookStoreJ-application, or from a docker image you have to use the public IP address of the machine running BookStoreJ-application. 

As result, you should get following overview with available operations: 

![OpenAPI-Docu](images/OpenAPI-Docu.png) 

Now, lets start execute some methods.
       
#### Get list of books

To get a list of available books, you first have to select the corresponding methods. As soon as you pressed it, you will get detailed information such as including parameters and responses. 

![OpenAPI-getBooks](images/OpenAPI-getBooks.png) 

Now, you can execute the method `getBooks` and Swagger-UI will show you all available books. 

![OpenAPI-getBooksResult](images/OpenAPI-getBooksResult.png) 

From these results you can play around with remaining methods provided by this application. 

That’s it! 
