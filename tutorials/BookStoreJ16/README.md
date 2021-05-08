# BookStoreJ16 

## What You Learn

*   How and why to use annotations in an application core 
*   How to improve semantic meaning of DDD pattern elements using Java records
*   Use of annotations to improve reusability     

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
solution for implementing the DDD pattern elements. In previous tutorial `BookStoreJ` we saw that annotations can help to indicate a specific pattern 
element. In addition, you should try to use the best possible syntax provided by the programming language to focus on the semantic meaning of the 
source code.  

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

As you can see, all important information of a DomainEvent can be seen in thw following two lines.

*   `@DomainEvent`: Indicates the concrete type of the pattern element.  
*   `public record BookSoldOut(ISBN13 isbn13)`: Indicates the type name `BookSoldOut` including the provided data which is `ISBN13` 

### Implementing `IDomainEventPublisher` 

In large applications it is quite common that you have multiple domain events that have to published to other applications. 
To solve this issue at least following solutions exist: 

*   Method overloading: Provide a specific method for each type of DomainEvent in `IDomainEventPublisher`. On the one side, this ensures static type
    safety but could flood your interface if the number of domain events is quite large. Unfortunately, I've learned that this could also lead to
    implementations in a `DrivenAdapter` in which each domain event is treated in a slightly different way. 

*   Abstract `DomainEvent` class: This allows to ensure type safety in `IDomainEventPublisher` and also providing only a single method that is
    implemented in a generic way. This seems to solve all issues from method overloading. The problem with this approach is that you introduce an
    interface that must be implemented by all kind of domain events for technical reason. At first glance, this seems to be a slightly esoteric 
    problem. In the long run, I've learned that such classes can be a gate opener, allowing technology aspects to enter the application core.

*   Publishing an `Object`: An alternative solution is to provide a method accepting a domain event of type `Object`. This prevents entering 
    technology aspects into the application core. The obvious drawback is that you loose type safety. In case you annotated all your classes you can
    double-check if the domain event is annotated with `DomainEvent`. This prevents publishing arbitrary objects, but this check is performed only 
    during runtime.
    
Of course, you can also combine the approaches. For example, you can use method overloading, and the implementation uses an internal method accepting
an `Object`. Anyway, the most important aspects are: 
*   The outbound port is an interface to ensure the separation of your application core from a technology stack.
*   To avoid entering technology aspects into the application core, or vice versa, you should provide a clean guideline how to handle this.   
    
Please do not underestimate such aspects if your application runs for several decades and is maintained by different developer teams. So you should
discuss and docment such aspects with your colleagues and/or software architects. 

Finally, the following code shows how to use the annotation to add a runtime test. 

```java
@DrivenAdapter
public class DomainEventPublisher implements IDomainEventPublisher
{
private final MessageSender messageSender;

    public DomainEventPublisher(Properties properties)
    {
        messageSender = MessageSenderManager.getMessageSender(properties);
    }

    @Override
    public void publish(Object domainEvent)
    {
        validateDomainEvent(domainEvent);
        messageSender
                .send(domainEvent)
                .toTopic("BookStoreTopic")
                .asJson();
    }

    private void validateDomainEvent(Object domainEvent)
    {
        Objects.requireNonNull(domainEvent);
        if ( domainEvent.getClass().getAnnotation(DomainEvent.class) == null )
        {
            throw new IllegalArgumentException("Given object is not annotated with @DomainEvent");
        }
    }

}
```

## Implement the Application
   
Implementing the application using java records is almost the same as using standard classes. The only difference we need to take into account 
is to ensure proper Json serialization and deserialization. Especially deserialization could be an issue due to the immutability of records and their
final fields. 

Jexxa uses Gson library for Json serialization by default which does not provide native support of java records so far. Therefore, we have to 
provide a generic type factory for records. A generic implementation can be found [here](src/main/java/io/jexxa/tutorials/bookstorej16/infrastructure/support/J16JsonConverter.java).

Within the main method, we have to set this special JSonConverter as you can see in the following snippet.  

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
