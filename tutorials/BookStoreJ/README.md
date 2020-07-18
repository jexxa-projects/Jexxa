# BookStore 

## What You Learn

*   How and why to use annotations in an application core 
*   How to handle cross-cutting concerns within the application core        

## What you need

*   Understand tutorial `BookStore` because we explain only new aspects 
*   30 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.6 (or higher) installed
*   curl or jconsole to trigger the application
*   A postgres DB (if you start the application with option `-jdbc')  

## Implementing Application Core 

### 1. Mapping to DDD patterns  
TODO: Explain annotations 

### A note on implementing DDD patterns  
TODO: Explain cross cutting concerns  

*   `ValueObject` and `DomainEvent`: Are immutable and compared based on their internal values
    *   They must not have setter methods. So all fields should be final. 
    *   They must provide a valid implementation of equals() and hashcode()
    *   They include no business logic, but they have to validate their input data    

*   `Aggregate`: Is identified by a unique `AggregateID` which is a `ValueObject`
    *   `Book` uses an `ISBN13` object     

*   `Repositroy` when defining any interface within the application core ensure that you use the domain language for all methods. Resist the temptation to use the language of the used technology stack that you will use to implement this interface.        
     
## 2. Implement the Infrastructure

Implementation of `IDomainEventPublisher` just prints the `DomainEvent` to the console. So we can just use the implementation from tutorial `TimeService`.    



That's it. 

## Compile & Start the Application with console output 

```console                                                          
mvn clean install
java -jar target/bookstore-jar-with-dependencies.jar 
```
You will see following (or similar) output
```console
[main] INFO io.jexxa.tutorials.bookstore.BookStoreApplication - Use persistence strategy: IMDBRepository 
[main] INFO io.jexxa.core.JexxaMain - Start BoundedContext 'BookStoreApplication' with 2 Driving Adapter 
[main] INFO org.eclipse.jetty.util.log - Logging initialized @474ms to org.eclipse.jetty.util.log.Slf4jLog
[main] INFO io.javalin.Javalin - Starting Javalin ...
[main] INFO io.javalin.Javalin - Listening on http://localhost:7000/
[main] INFO io.javalin.Javalin - Javalin started in 148ms \o/
[main] INFO io.jexxa.core.JexxaMain - BoundedContext 'BookStoreApplication' successfully started in 0.484 seconds
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