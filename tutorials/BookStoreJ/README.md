# BookStoreJ 

## What You Learn

*   How and why to use annotations in an application core 
*   How to handle cross-cutting concerns within the application core        

## What you need

*   Understand tutorial `BookStore` because we explain only new aspects 
*   60 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.6 (or higher) installed
*   curl or jconsole to trigger the application
*   A postgres DB (if you start the application with option `-jdbc')  

## Implementing Application Core 

### A pattern language for your application core 
In the [architecture of Jexxa](https://repplix.github.io/Jexxa/jexxa.html) we describe that Jexxa does not require any special annotations. Main reason is that framework related annotations can tightly couple your application core to a specific technology stack. Therefore, framework specific annotations should not be used within the application core. 

On the other side you can use annotations as pure meta-information within your developing teams, especially to make a so called __pattern language__ explicit. The pattern language is part of the micro architecture of an application and allows your developers to quickly navigate through the source code. Instead of reading and understanding the source code again and again, a developer can navigate through the code based on these patterns. For example if the application uses the pattern language of DDD, and you have to change the business logic of your application core the corresponding code must be within an `Aggregate`. So you can directly navigate to the `Aggregate` and skip all remaining elements. 

Even if you not use the pattern language of DDD, the developers typically used some patterns to implement the application. To make these patterns explicit, I strongly recommend annotating all classes within the application core with their corresponding element of the pattern language. Classes that cannot be assigned to a specific element typically violate some design principles such as the single responsibility principle. In case of a durable software system, you will get the following advantages: 

*   You document the patterns directly in the code which ensures that all developers will read it
*   You establish a common understanding of the used patterns within the development team 
*   A developer will get a guideline how to integrate a new feature 
*   You speed up relearning of and navigating within the source code   
*   You speed up and simplify the initial training of new employees
*   You can use them for code reviews and refactorings      

For the pattern langauge of DDD we recommend project [Addend](https://addend.jexxa.io/).     

The following shows the annotation of an `Aggregate`. Apart from the obvious annotation, it also uses two other annotations: 
*   `AggregateID` to explicitly document the unique key
*   `AggregateFactory` to explicitly document the factory method for the `Aggregate`     

```java
@Aggregate
public final class Book
{
    private final ISBN13 isbn13;
    private int amountInStock = 0;

    private Book(ISBN13 isbn13)
    {
        this.isbn13 = isbn13;
    }

    @AggregateID
    public ISBN13 getISBN13()
    {
        return isbn13;
    }
  
    // ... 

    @AggregateFactory(Book.class)
    public static Book newBook(ISBN13 isbn13)
    {
        return new Book(isbn13);
    }
}
```
 
### Cross-cutting concerns   

When applying the tactical patterns of DDD and map the ubiquitous language into the application core, it can happen that you get a lot of small classes. Especially `ValueObject` classes are affected. For all these classes you have to implement valid cross-cutting concerns such as `equals()`, `hashCode()`, and `toString()`.

Even though today's IDE's can automatically generate these methods they can bloat your source code and much worse hide the domain specific aspects. In addition, you must not forget to recreate these methods if you change the attributes of the class. 

To resolve these issues I recommend `AspectJ` for realizing cross-cutting concerns so that they are not visible in the source code of your application core. Since we already annotated all our classes with our pattern language, we can reuse these annotations. 

Important note: This decision is a trade-off between using a technology stack within the application core to make the ubiquitous language more explicit. Regardless of the outcome, you should discuss this point with your developers and software architects and document the decision.  

In case you would like to use AspectJ together with pattern language of DDD we recommend project [AddendJ](https://addendj.jexxa.io/).     

In the following you see the implementation of class `ISBN13` without an implementation of equals and hashcode. These methods are weaved into the source code during compile time.    
 
```java
@ValueObject
public class ISBN13
{
 private final String value;

 public ISBN13(String value)
 {
     Objects.requireNonNull(value);
     validateChecksum(value);

     this.value = value;
 }

 public String getValue()
 {
     return value;
 }
 
 private void validateChecksum(String isbn13)
 {
   //..
 }
 
}  
```

## Implement the Application

If your application core is annotated with your pattern language, you can use it together wih Jexxa. This requires to changes in contrast to initial `BookStore` application.
1.  You have to add package names providing your annotated classes to Jexxa by using method `addToApplicationCore`. This is required because Jexxa scans only the specified package names.    
2.  You have to bind driving adapters using method `bindToAnnotation`. In this case alle inbound ports annotated with given annotation are bind to the driving adapter.    

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
        // Define the default strategy which is either an IMDB database or a JDBC based repository
        // In case of JDBC we use a simple key value approach which stores the key and the value as json strings.
        // Using json strings might be very inconvenient if you come from typical relational databases but in terms
        // of DDD our aggregate is responsible to ensure consistency of our data and not the database.
        RepositoryManager.setDefaultStrategy(getDrivenAdapterStrategy(args));

        JexxaMain jexxaMain = new JexxaMain(BookStoreJApplication.class.getSimpleName());

        jexxaMain
                // In order to find ports by annotation we must add packages that are searched by Jexxa.
                // Therefore, we must also add inbound ports to application core 
                .addToApplicationCore(INBOUND_PORTS)
                .addToApplicationCore(OUTBOUND_PORTS)
                .addToInfrastructure(DRIVEN_ADAPTER)

                //Get the latest books when starting the application
                .bootstrap(ReferenceLibrary.class).with(ReferenceLibrary::addLatestBooks)

                // In case you annotate your domain core with your pattern language,
                // You can also bind DrivingAdapter to annotated classes.
                .bind(RESTfulRPCAdapter.class).toAnnotation(ApplicationService.class)
                .bind(JMXAdapter.class).toAnnotation(ApplicationService.class)

                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())

                .start()

                .waitForShutdown()

                .stop();
    }

}  
```

## Run the application  

### Use an in memory database

```console                                                          
mvn clean install
java -jar target/bookstorej-jar-with-dependencies.jar 
```
You will see following (or similar) output
```console
[main] INFO io.jexxa.tutorials.bookstorej.BookStoreApplication - Use persistence strategy: IMDBRepository 
[main] INFO io.jexxa.core.JexxaMain - Start BoundedContext 'BookStoreApplication' with 2 Driving Adapter 
[main] INFO org.eclipse.jetty.util.log - Logging initialized @474ms to org.eclipse.jetty.util.log.Slf4jLog
[main] INFO io.javalin.Javalin - Starting Javalin ...
[main] INFO io.javalin.Javalin - Listening on http://localhost:7000/
[main] INFO io.javalin.Javalin - Javalin started in 148ms \o/
[main] INFO io.jexxa.core.JexxaMain - BoundedContext 'BookStoreApplication' successfully started in 0.484 seconds
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
[main] INFO io.jexxa.tutorials.bookstorej.BookStoreApplication - Use persistence strategy: JDBCKeyValueRepository 
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
