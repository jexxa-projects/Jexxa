# BookStoreJ16 

## What You Learn

*   How and why to use annotations in an application core 
*   How to handle cross-cutting concerns within the application core using Java records        

## What you need

*   __JDK 16 (or higher) installed__
*   A basic understanding of Java records 
*   Understand tutorial `BookStoreJ` because we explain only new aspects 
*   60 minutes
*   Maven 3.6 (or higher) installed
*   curl or jconsole to trigger the application
*   A postgres DB (if you start the application with option `-jdbc')  

## Implementing Application Core 

### Use of Java records

When implementing a business application using DDD one of the most important aspects is to provide a semantically elegant and consistent 
solution for implementing the DDD pattern elements. Apart from using annotations, you should try to use the best possible syntax provided by 
the programming language to focus on the semantic meaning of the source code. 

One of the major changes in Java 16 is the official support for Java [records](https://openjdk.java.net/jeps/359). They are especially designed for classes holding immutable data. Apart from a compact syntax they also provide valid implementations of `equals()`, `hashCode()`, and `toString()`. 
Therefore, they are suitable for the following DDD elements. 

*   `ValueObject`
*   `DomainEvent`

The following example shows the implementation of the domain event `BookSoldOut` using a record. Note that the static method `bookSoldOut` is not required but improves the read flow when creating the domain event.

```java 
@DomainEvent
public record BookSoldOut(ISBN13 isbn13)
{
    public static BookSoldOut bookSoldOut(ISBN13 isbn13)
    {
        return new BookSoldOut(isbn13);
    }
}
```

## Implement the Application
   
Implementing the application using java records is almost the same as using standard classes. The only difference we need to take into account 
is to ensure proper Json serialization and deserialization. Especially deserialization could be an issue due to the immutability of records and their
final fields. 

Jexxa uses Gson library for Json serialization by default which does not provide native support of java records. Therefore, we have to provide a generic
type factory for records. A generic implementation can be found [here](src/main/java/io/jexxa/tutorials/bookstorej16/infrastructure/support/J16JsonConverter.java).  

```java 
public final class BookStoreJApplication
{
    //Declare the packages that should be used by Jexxa
    private static final String DRIVEN_ADAPTER  = BookStoreJApplication.class.getPackageName() + ".infrastructure.drivenadapter";
    private static final String OUTBOUND_PORTS  = BookStoreJApplication.class.getPackageName() + ".domainservice";
    //Add also package name with inbound ports so that they are scanned by Jexxa
    private static final String INBOUND_PORTS   = BookStoreJApplication.class.getPackageName() + ".applicationservice";

    public static void main(String[] args)
    {
        //Set a JsonConverter that support java records
        JSONManager.setJSONConverter(new J16JsonConverter());
   
        ...
    }

}  
```

## Run the application  

### Use an in memory database

```console                                                          
mvn clean install
java -jar target/bookstorej16-jar-with-dependencies.jar 
```
You will see following (or similar) output
```console
[main] INFO io.jexxa.tutorials.bookstorej16.BookStoreJ16Application - Use persistence strategy: IMDBRepository 
[main] INFO io.jexxa.tutorials.bookstorej16.BookStoreJ16Application - Use messaging strategy: MessageLogger 
[main] INFO org.eclipse.jetty.util.log - Logging initialized @429ms to org.eclipse.jetty.util.log.Slf4jLog

[main] INFO io.javalin.Javalin - Listening on http://0.0.0.0:7000/
[main] INFO io.javalin.Javalin - Javalin started in 92ms \o/
[main] INFO io.javalin.Javalin - OpenAPI documentation available at: http://0.0.0.0:7000/swagger-docs
[main] INFO io.jexxa.core.JexxaMain - BoundedContext 'BookStoreJ16Application' successfully started in 0.754 seconds

```          

### Use a Postgres database

You can run this application using a Postgres database because the corresponding driver is included in the pom file. The 
configured username and password is `admin`/`admin`. You can change it in the [jexxa-application.properties](src/main/resources/jexxa-application.properties) 
file if required.       

```console                                                          
mvn clean install
java -jar target/bookstorej-jar-with-dependencies.jar -jdbc 
```
In contrast to the above output Jexxa will state that you use JDBC persistence strategy now:
```console
[main] INFO io.jexxa.tutorials.bookstorej16.BookStoreJ16Application - Use persistence strategy: JDBCKeyValueRepository 
```

Note: In case you want to use a difference database, you have to: 

1.  Add the corresponding jdbc driver to [pom.xml](pom.xml) to dependencies section.
2.  Adjust the section `#Settings for JDBCConnection to postgres DB` in [jexxa-application.properties](src/main/resources/jexxa-application.properties).

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
