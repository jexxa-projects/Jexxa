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

### A pattern language for your application core 
In the [documentation of Jexxa](https://repplix.github.io/Jexxa/jexxa.html) we describe that Jexxa does not require any special annotations. Main reason is that framework related annotations can tightly couple your application core to a specific technology stack. Therefore, such annotations should not be used within the application. 

On the other side you can use annotations as pure meta-information within your developing teams, especially to make a so called __pattern language__ explicit. The pattern language is part of the micro architecture of an application and allows your developers to quickly navigate through the source code.  Instead of reading the source code a developer can navigate through the code based on the patterns. 

For example if the application uses the pattern language of DDD, and you have to change the business logic of your application core the corresponding code must be within an aggregate. So you can directly navigate to the `Aggregate` and skip all remaining elements.      

Therefore, we strongly recommend annotating all classes within the application core with their corresponding element of the pattern language. Classes that cannot be assigned to a specific element violate the single responsibility principle.

For the pattern langauge of DDD we recommend project [Addend](https://addend.jexxa.io/).     

The following shows the annotation of an 'Aggregate'. Apart from the obvious annoatation, it also uses two other annotations: 
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

When applying the tactical patterns of DDD and map the ubiquituous language into the application core, it can happen that you get a lot of small classes. Especially `ValueObject` classes are affected you have to implement valid cross-cutting concerns for these objects such as `equals()`, `hashCode()` and `toString()`.

These methods can bloat your source code and much worse hide the domain specific aspects. To resolve this issue we recommend AspectJ for realizing cross-cutting concerns. Especially if we already annotate all our classes, we can use these annotations. 

Important note: This is weighing up between using some kind of technology-stack on the one side to hide technology specific issues and to make the ubiquituos language more explicit on the other side.     

In case you would like to use AspectJ together with pattern language of DDD we recommend project [AddendJ](https://addendj.jexxa.io/).     

In the following you see the implementation of class `ISBN13` without an implementation of equals and hashcode. Theses methods are weaved into the source code during compile time.    
 
```java
@ValueObject
public class ISBN13
{
 private final String value;

 public ISBN13(String value)
 {
     Validate.notNull(value);
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
