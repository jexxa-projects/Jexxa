# Contract Management 

## What You Learn

*   When to use Jexxa's Repository or ObjectStore
*   How to use Jexxa's ObjectStore

## What you need

*   Understand tutorial `BookStoreJ` because we explain only new aspects
*   30 minutes
*   JDK 11 (or higher) installed
*   Maven 3.6 (or higher) installed
*   curl or jconsole to trigger the application
*   A postgres DB (if you start the application with option `-jdbc')

## Motivation 

When developing an enterprise application you should focus on how the business domain and how to represent
it within your application. Technical aspects such as the database schema should be hidden as good as possible. 
Within Jexxa we try to support this by providing strategies for implementing a Repository.   

### When to choose `IRepository`  

The `IRepository` interface is very limited regarding querying a managed object. Either you query an object by its unique key, or you request all 
objects. If you want to offer advanced querying mechanisms, you have to implement them by yourself based on querying all objects. Even though this 
sounds very limiting choosing an `IRepository` for your object should be your first choice. Especially during development phase of a new application or
bounded context, it gives you the time to learn which query interface you really need from the applications point of view. 

In general, you should use an `IRepository` in following scenarios: 

* You query the managed objects only be their unique key.
* In case you need more advanced query operations, the lifetime of the managed objects is short, so that the amount of managed objects is relatively small.

Especially the second case happens quite often in production systems, especially in batch systems. Here, it is quite common that software controlling 
a specific manufacturing unit requires only to know the batches that are currently processed. As soon as the processing step is finished, a corresponding 
`DomainEvent` is published and the software object can be removed from the repository.         
                                                                                      
Please do not underestimate this aspect because it supports you separating your production data from your archive data.  

### When to choose `IObjectStore`
                               
The `IObjectStore` provides more sophisticated interfaces to query managed objects by all kind of data. Available strategies make explicit use
of optimization mechanism of the underlying technology so that the performance depends on chosen technology stack. This kind of repository should 
be your second choice. As soon as you see that an `IRepository` is not sufficient, you should switch the implementation of the driven adapter to an 
`IObjectStore`. Please note that this step should be transparent to your application core because it uses a single interface which is not affected. 
Only the underlying strategy is changed.   

In general, you should use an `IObjectStore` in following scenarios:

* You need several ways to request managed objects and
* the lifetime of the managed objects is high, so that the amount of managed objects will continuously increase.
* The metadata to find objects is fixed and will not change over time.

At first thought, the last requirement sounds like a severe restriction. Especially this kind of change typically happens some time after the 
software is in production. But please keep in mind that your application core is protected by your application specific interface. So changing the 
implementation will not affect the application core itself. In addition, you have a lot of knowledge based from production and change requests which 
underlying technology or database stack should be used. Now it is the right point in time to switch to a specific implementation without using a 
specific strategy or to provide your own strategy using technologies such as liquibase for versioning your database schema.    

Typical use cases to select an `IObjectStore` are:
* An archive of the domain events.
* A bounded context managing objects with a very long lifetime such as contracts.

### Strategies for `IRepository` and `IObjectStore`

At the moment, Jexxa provides driven adapter strategies for in memory storage and JDBC. To query an `IRegistry` or `IObjectStore` you use the
`RegistryManager` or `ObjectStoreManager` respectively. A significant advantage of using these strategies is to write tests against your 
Repository without the need of a database. This typically speed up your tests significantly.   

By default, both manager classes select a strategy depending on your application configuration and the `Property` object passed to Jexxa as follows: 

1. Check if the application defined a strategy for a specific object type is registered.
2. Check if the application defined a default strategy for all kind of objects. 
3. Check if the `Property` object defines a JDBC driver. In this case the `JDBCKeyValueRepository` or `JDBCObjectStore` is used.
4. Otherwise, an in memory strategy `IMDBKeyValueRepository` or `IMDBObjectStore` is used. 

## Example ContractManagement

This tutorial defines following requirements: 
* `IContractRepository`: Manage contracts with a very high lifetime and must be searched by different metadata.  
* `IDomainEventStore`: Archive all domain events that must be searched by different metadata.    

Based on the requirements, both interface should be implemented using an `IObjectStore`.

                                                                                        

