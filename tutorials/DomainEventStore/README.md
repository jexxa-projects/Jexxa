# DomainEventStore 

General note: This is just to show you how Jexxa Uses DomainEvents. 

* This is not intended to an EventStore in terms of CQRS/Aggregates
* This application is not intended to show how to develop a DomainEventStore. As soon as you need a 
* DomainEventStore check your requirements and evaluate other messaging frameworks such as Kafka.  

THE INTENTION OF THIS TUTORIALS IS TO SHOW HOW TO WORK WITH `DomainEventContainer` as it is used in Jexxa.  

## What You Learn

*   How Jexxa represents DomainEvents 

## What you need

*   Understand tutorial `BookStore` because we explain only new aspects 
*   60 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.6 (or higher) installed
*   curl or jconsole to trigger the application
*   A postgres DB (if you start the application with option `-jdbc')  

## What is a `DomainEventContainer`

A `DomainEventContainer` includes a single `DomainEvent` that occurred and was published by an application.

The structure of the `DomainEventContainer` is as follows: 

```json
{
    "uuid": "string",
    "payloadType": "string",
    "payload": "string",
    "publishedAt": {
       "seconds": 0,
       "nanos": 0
    }
}
```  
An example of such a `DomainEvent` is
  
```json
{
  "uuid": "b01ebd0a-c45c-423b-911a-cd8922525fcd",
  "payloadType": "io.jexxa.tutorials.bookstorej.domain.domainevent.BookSoldOut",
  "payload": "{\"isbn13\":{\"value\":\"978-1-60309-025-4\"}}",
  "publishedAt": {
    "seconds": 1606644442,
    "nanos": 287678000
  }
}
```  
 
## Receiving a domain event  
 
## Replay a domain event 


