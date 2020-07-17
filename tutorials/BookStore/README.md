# BookStore 

## What You Learn

*   Default package structure for more complex applications based on DDD   
*   How to provide an implementation of a specific outbound-port which is called `Repository` in terms of DDD 

## What you need

*   Understand tutorial `HelloJexxa` ans `TimeService` because we explain only new aspects 
*   60 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.6 (or higher) installed
*   A running ActiveMQ instance (at least if you start the application with JMS)
*   curl or jconsole to trigger the application  

## Functionality of the BookStore
This application should provide following functionality

*   Manage available books in store
    *   Number of available books in store  
    *   Send a DomainEvent if a book is sold out which means that last book was sold.
    *   All books should be identified by their ISBN13.
    *   The ISBN13 number should be represented with its defined components such as prefix, registrant, ... 

## Implementing Application Core 

### 1. Mapping to DDD patterns  
First we map the functionality of the application to DDD patterns   

*   `Aggregate`: Elements that change over time and include our business logic 
    *   `BookStock` because our stock will change        
    *   `Book` because it can go out of print. Since we manage only books that are in stock our BookStock is also the root aggregate for our books.  
    
*   `ValueObject`: Elements that represent a state and are immutable
    *   `StoreAddress` which identifies our stock for a specific store 
    *   `ISBN13` which identifies a book
     
*   `DomainEvents`: Business events that happened in the past 
    *   `BookOutOfPrint` when a book is no longer printed
    *   `BookSoldOut` when copies of a book are no longer in stock
    
*   'DomainService': 
    *   `DomainEventPublisher`: We need to publish our domain events in some way
    *   `BookStockRepository`: We have to persist our stock in some way
     
       
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
     
## 2. Implement the Infrastructure

TODO: Important note: Key of RepositoryManager must have a valid equals/hashcode implementation 

### Driven Adapter with JMS

## 3. Implement the Application 

Finally, we have to write our application. As you can see in the code below there are two main differences compared to `HelloJexxa`:

*   We define the packages that should be used by Jexxa. This allows fine-grained control of used driven adapter since we must offer only a single implementation for each outbound port. In addition, this limits the search space for potential driven adapters and speeds up startup time.
*   We do not need to instantiate a TimeService class explicitly. This is done by Jexxa including instantiation of all required driven adapter.   
   
```java

```  

That's it. 

## Compile & Start the Application with console output 


