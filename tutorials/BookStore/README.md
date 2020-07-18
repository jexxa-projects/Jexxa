# BookStore 

## What You Learn

*   How to provide an implementation of a specific outbound-port which is called `Repository` in terms of DDD using a database  
*   How to initialize master data into a Repository      
*   Default package structure for more complex applications based on DDD
*   How to test your business logic using Jexxa     


## What you need

*   Understand tutorial `HelloJexxa` ans `TimeService` because we explain only new aspects 
*   60 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.6 (or higher) installed
*   curl or jconsole to trigger the application
*   A postgres DB (if you start the application with option `-jdbc')  

## Requirements to the application core
This application core should provide following functionality:

*   Manage available books in store which means
    *   add new books
    *   sell books
    *   query operations about available books
       
*   All books should be identified by their ISBN13
*   For each book the store the umber of available copies   
*   Publish `DomainEvent` `BookSoldOut` if last copy of a book is sold
*   A service which gets the latest books from our reference library. For this tutorial it is sufficient that: 
    *   Service provides a hardcoded list  
    *   Service is triggered when starting the application     

## Implementing Application Core 

### 1. Mapping to DDD patterns  
First we map the functionality of the application to DDD patterns   

*   `Aggregate`: Elements that change over time and include our business logic 
    *   `Book` which manages available copies of a book.   
    
*   `ValueObject`: Elements that represent a state and are immutable
    *   `ISBN13` which identifies a book
     
*   `DomainEvent`: Business events that happened in the past 
    *   `BookSoldOut` when copies of a book are no longer in stock
    
*   'DomainService': 
    *   `IDomainEventPublisher`: We need to publish our domain events in some way
    *   `BookRepository`: Manage `Book` instances
    *   `ReferenceLibrary`: Return latest books
    
*   `BusinessException`:
    *   `BookNotInStockException`: In case we try to sell a book that is currently not available   
     
       
### Package structure 
When implementing your first applications using DDD with an onion architecture we recommend following package structure: 

*   applicationservice

*   domainservice

*   domain 
    *   valueobject
    *   aggregate
    *   domainevent
    *   businessexception
    
*   infrastructure
    *   drivenadapter
    *   drivingadapter (if required)

### A note on the implementation

*   `ValueObject` and `DomainEvent`: Are immutable and compared based on their internal values
    *   They must not have setter methods. So all fields should be final. 
    *   They must provide a valid implementation of equals() and hashcode()
    *   They include no business logic, but they have to validate their input data
    
*   `Aggregate`: Is identified by a unique `AggregateID` which is a `ValueObject`
    *   `Book` uses an `ISBN13` object     
     
## 2. Implement the Infrastructure

TODO: Important note: Key of RepositoryManager must have a valid equals/hashcode implementation 

### Driven Adapter with JDBC




## 3. Implement the Application 

Finally, we have to write our application. As you can see in the code below there are two main differences compared to `HelloJexxa`:

*   We define the packages that should be used by Jexxa. This allows fine-grained control of used driven adapter since we must offer only a single implementation for each outbound port. In addition, this limits the search space for potential driven adapters and speeds up startup time.
*   We do not need to instantiate a TimeService class explicitly. This is done by Jexxa including instantiation of all required driven adapter.   
   

That's it. 

## Compile & Start the Application with console output 


