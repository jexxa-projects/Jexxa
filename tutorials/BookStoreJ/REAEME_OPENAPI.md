# BookStoreJ - With OpenAPI support 

## What You Learn

*   How to access application from outside using OpenAPI        

## What you need

*   Understand tutorial `BookStoreJ` because we explain only new aspects 
*   60 minutes
*   JDK 11 (or higher) installed
*   [Swagger UI](https://swagger.io/tools/swagger-ui/)    
*   Maven 3.6 (or higher) installed
*   curl or jconsole to trigger the application
*   A postgres DB (if you start the application with option `-jdbc')  

## Benefits with strict separation  

 


## Run the application  

### Use an in memory database

```console                                                          
mvn clean install
java -Dio.jexxa.rest.open_api_path=swagger-docs -jar target/bookstorej-jar-with-dependencies.jar 
```
You will see following (or similar) output
```console
main] INFO io.jexxa.tutorials.bookstorej.BookStoreJApplication - Use persistence strategy: IMDBRepository 
[main] INFO org.eclipse.jetty.util.log - Logging initialized @459ms to org.eclipse.jetty.util.log.Slf4jLog
[main] INFO io.jexxa.core.JexxaMain - Start BoundedContext 'BookStoreJApplication' with 2 Driving Adapter 
[main] INFO io.javalin.Javalin - Starting Javalin ...
[main] INFO org.eclipse.jetty.server.Server - jetty-9.4.31.v20200723; built: 2020-07-23T17:57:36.812Z; git: 450ba27947e13e66baa8cd1ce7e85a4461cacc1d; jvm 11.0.2+9
[main] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@73a1e9a9{HTTP/1.1, (http/1.1)}{0.0.0.0:7000}
[main] INFO org.eclipse.jetty.server.Server - Started @1054ms
[main] INFO io.javalin.Javalin - Listening on http://0.0.0.0:7000/
[main] INFO io.javalin.Javalin - Javalin started in 134ms \o/
[main] INFO io.jexxa.core.JexxaMain - BoundedContext 'BookStoreJApplication' successfully started in 0.894 seconds
```          

### Execute some commands using curl 

#### Get list of books

Command: 
```Console
curl -X GET  http://localhost:7000/BookStoreService/getBooks
```

Response: 
```Console
[{"value":"978-1-891830-85-3"},{"value":"978-1-60309-025-4"},{"value":"978-1-60309-016-2"},{"value":"978-1-60309-265-4"},{"value":"978-1-60309-047-6"},{"value":"978-1-60309-322-4"}]
```

#### Ask if a specific book is in stock**

Command:
```Console
curl -X POST -H "Content-Type: application/json" \
    -d '"978-1-891830-85-3"' \
    http://localhost:7000/BookStoreService/inStock                 
```

Response: 
```Console
false
```

#### Add some books

Command:
```Console
curl -X POST -H "Content-Type: application/json" \
    -d '["978-1-891830-85-3", 5]' \
    http://localhost:7000/BookStoreService/addToStock                 
```
Response: No output  
```Console
```

#### Ask again if a specific book is in stock

Command:
```Console
curl -X POST -H "Content-Type: application/json" \
    -d '"978-1-891830-85-3"' \
    http://localhost:7000/BookStoreService/inStock                 
```

Response: 
```Console
true
```
