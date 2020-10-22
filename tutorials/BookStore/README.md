# BookStore 

## What You Learn

*   How to provide an implementation of a specific outbound-port which is called `Repository` in terms of DDD using a database  
*   How to initialize master data into a Repository      
*   Default package structure for more complex applications based on DDD
*   How to test your business logic using Jexxa     

## What you need

*   Understand tutorial `HelloJexxa` and `TimeService` because we explain only new aspects 
*   60 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.6 (or higher) installed
*   curl or jconsole to trigger the application
*   A postgres DB (if you start the application with option `-jdbc`)  

## Requirements to the application core
This application core should provide following super simplified functionality:

*   Manage available books in store which means to add, sell, and query books 
*   All books should be identified by their ISBN13
*   For each book the store the umber of available copies   
*   Publish `DomainEvent` `BookSoldOut` if last copy of a book is sold
*   A service which gets the latest books from our reference library. For this tutorial it is sufficient that: 
    *   Service provides a hardcoded list  
    *   Service is triggered when starting the application     

## Implementing Application Core 

General note: There are several books, courses, tutorials available describing how to implement an application core using the patterns of DDD. 
The approach used in this tutorial should not be considered as reference. It serves only for demonstration purpose how to realize your decisions 
with Jexxa.       

### 1. Mapping to DDD patterns  
First we map the functionality of the application to DDD patterns   

*   `Aggregate:` Elements that change over time and include our business logic 
    *   `Book` which manages available copies of a book.       

*   `ValueObject:` Elements that represent a state and are immutable
    *   `ISBN13` which identifies a book     

*   `DomainEvent:` Business events that happened in the past 
    *   `BookSoldOut` when copies of a book are no longer in stock   

*   `DomainService:` 
    *   `IDomainEventPublisher:` We need to publish our domain events in some way. Since the implementation requires a technology stack we can only define an interface.   
    *   `IBookRepository:` Interface to manage `Book` instances. Since the implementation requires a technology stack we can only define an interface.  
    *   `ReferenceLibrary:` Return latest books. For simplicity, we assume that it is a service which does not related to our domain core directly.             

*   `BusinessException:`
    *   `BookNotInStockException:` In case we try to sell a book that is currently not available   
     
       
### Package structure 
In our tutorials we use following package structure: 

*   applicationservice

*   domainservice

*   domain 
    *   valueobject
    *   aggregate
    *   domainevent
    *   businessexception    

*   infrastructure
    *   drivenadapter
    *   drivingadapter 

### A note on implementing DDD patterns  

*   `ValueObject` and `DomainEvent`: Are immutable and compared based on their internal values
    *   They must not have setter methods. So all fields should be final. 
    *   They must provide a valid implementation of equals() and hashcode()
    *   They include no business logic, but they have to validate their input data    

*   `Aggregate`: Is identified by a unique `AggregateID` which is a `ValueObject`
    *   `Book` uses an `ISBN13` object     

*   `Repositroy` when defining any interface within the application core ensure that you use the domain language for all methods. Resist the temptation to use the language of the used technology stack that you will use to implement this interface.        
     
## 2. Implement the Infrastructure

Implementation of `IDomainEventPublisher` just prints the `DomainEvent` to the console. So we can just use the implementation from tutorial `TimeService`.    

### Implement the Repository 
When using Jexxa's `RepositoryManager` implementing a repository is just a mapping to the `IRepository` interface which provides typical CRUD operations.  
  
The requirement are: 

*   The managed object provides a so called key-function which returns a key to uniquely identify the object. In case of this tutorial it is the method `getISBN`.
*   The key itself must provide a valid implementation of method equals and hashcode to validate equality.     

The following source code shows a typical implementation of a `Repository`. Within the main function you can configure the `RepositoryManager` if required. 

For the sake of completeness we use a static factory method in this implementation instead of a public constructor. Here it is quite important to return the interface and not the concrete type.        

```java
  
@SuppressWarnings("unused")
public final class DDHBookRepository implements IBookRepository
{
    private final IRepository<Book, ISBN13> repository;

    private BookRepository(IRepository<Book, ISBN13> repository)
    {
        this.repository = repository;
    }              

    // Factory method that requests a repository strategy from Jexxa's RepositoryManager 
    public static IBookRepository create(Properties properties)
    {
        return new BookRepository(
                RepositoryManager.getInstance().getStrategy(Book.class, Book::getISBN13, properties)
        );
    }

    @Override
    public void add(Book book)
    {
        repository.add(book);
    }

    @Override
    public Book get(ISBN13 isbn13)
    {
        return repository.get(isbn13).orElseThrow();
    }

    @Override
    public boolean isRegistered(ISBN13 isbn13)
    {
        return search(isbn13)
                .isPresent();
    }

    @Override
    public Optional<Book> search(ISBN13 isbn13)
    {
        return repository.get(isbn13);
    }

    @Override
    public void update(Book book)
    {
        repository.update(book);
    }

    @Override
    public List<Book> getAll()
    {
        return repository.get();
    }
}
```

## 3. Implement the Application 

Finally, we have to write our application. As you can see in the code below there are two main differences compared to `HelloJexxa` and `TimeService`:

*   Define a default strategy for our Repositories.
*   Add a bootstrap service which is directly called to initialize domain-specific aspects.   
   
```java
    
public final class BookStoreApplication
{
    //...
    public static void main(String[] args)
    {
        // Define the default strategy which is either an IMDB database or a JDBC based repository
        // In case of JDBC we use a simple key value approach which stores the key and the value as json strings.
        // Using json strings might be very inconvenient if you come from typical relational databases but in terms
        // of DDD our aggregate is responsible to ensure consistency of our data and not the database.
        RepositoryManager.getInstance().setDefaultStrategy(getDrivenAdapterStrategy(args));
    
        JexxaMain jexxaMain = new JexxaMain(BookStoreApplication.class.getSimpleName());
    
        jexxaMain
                //Define which outbound ports should be managed by Jexxa
                .addToApplicationCore(OUTBOUND_PORTS)
                .addToInfrastructure(DRIVEN_ADAPTER)
    
                //Get the latest books when starting the application
                .bootstrap(ReferenceLibrary.class).with(ReferenceLibrary::addLatestBooks)
    
                .bind(RESTfulRPCAdapter.class).to(BookStoreService.class)
                .bind(JMXAdapter.class).to(BookStoreService.class)
    
                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())
                .bind(RESTfulRPCAdapter.class).to(jexxaMain.getBoundedContext())
    
                .start()
    
                .waitForShutdown()
    
                .stop();
    }
    //...
}
```

That's it. 

## Run the application with an in memory database

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

#### Query available books

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
## 4. Write some tests
Writing some tests with Jexxa is quite easy. If you implement your driven adapters using Jexxa's driven adapter strategies you can use 
package **jexxa-test**. It automatically provides stubs so that you do not need any mock framework. Main advantages are: 

*   You can focus on domain logic within your tests.
*   You don't need to use mocks which can lead to validating execution steps within the domain core instead of validating the use cases
*   Your tests are much easier to read. 
*   You can write your tests first without considering the implementation.   

First, add the following dependency to your tests. 

```maven
    <dependency>
      <groupId>io.jexxa.jexxatest</groupId>
      <artifactId>jexxa-test</artifactId>
      <version>2.5.0</version>
      <scope>test</scope>
    </dependency>
```

Following code shows a simple validation of our BookStoreService. Some additional tests can be found [here](https://github.com/repplix/Jexxa/blob/master/tutorials/BookStore/src/test/java/io/jexxa/tutorials/bookstore/applicationservice/BookStoreServiceTest.java).       

```java
class BookStoreServiceTest
{
    private static final String DRIVEN_ADAPTER = BookStoreApplication.class.getPackageName() + ".infrastructure.drivenadapter";
    private static final String DOMAIN_SERVICE = BookStoreApplication.class.getPackageName() + ".domainservice";

    private static final ISBN13 ISBN_13 = new ISBN13( "978-3-86490-387-8" );
    private static JexxaMain jexxaMain;
    
    private BookStoreService objectUnderTest;
    private MessageRecorder publishedDomainEvents;
    private IBookRepository bookRepository;


    @BeforeAll
    static void initBeforeAll()
    {
        // We recommend to instantiate JexxaMain only once for each test class.
        // If you have larger tests this speeds up Jexxa's dependency injection 
        // Note: For unit-tests you just need to bind any driving adapter  
        jexxaMain = new JexxaMain(BookStoreServiceTest.class.getSimpleName());
        jexxaMain.addToInfrastructure(DRIVEN_ADAPTER)
                .addToApplicationCore(DOMAIN_SERVICE);
    }

    @BeforeEach
    void initTest()
    {
        // JexxaTest is created for each tests. It provides and cleans up stubs before each test
        // Actually, JexxaTest provides stubs for repositories and send messages
        JexxaTest jexxaTest = new JexxaTest(jexxaMain);

        // Query a message recorder for an interface which is defines in your application core.
        publishedDomainEvents = jexxaTest.getMessageRecorder(IDomainEventPublisher.class);
        // Query the repository that is internally used.
        bookRepository = jexxaTest.getRepository(IBookRepository.class);
        // Query the application service we want to test.
        objectUnderTest = jexxaTest.getInstanceOfPort(BookStoreService.class);
    }

    @Test
    void receiveBook()
    {
        //Arrange
        var amount = 5;

        //Act
        objectUnderTest.addToStock(ISBN_13.getValue(), amount);

        //Assert - Here you can also use all the interfaces for driven adapters defined in your application without running the infrastructure
        assertEquals( amount, objectUnderTest.amountInStock(ISBN_13) );
        assertEquals( amount, bookRepository.get( ISBN_13 ).amountInStock() );
        assertTrue( publishedDomainEvents.isEmpty() );
    }


    @Test
    void sellLastBook() throws BookNotInStockException
    {
        //Arrange
        objectUnderTest.addToStock(ISBN_13.getValue(), 1);

        //Act
        objectUnderTest.sell(ISBN_13);

        //Assert - Here you can also use all the interfaces for driven adapters defined in your application without running the infrastructure
        assertEquals( 0 , objectUnderTest.amountInStock(ISBN_13) );
        assertEquals( 1 , publishedDomainEvents.size() );
        assertEquals( bookSoldOut(ISBN_13), publishedDomainEvents.getMessage(BookSoldOut.class));
    }

} 
```