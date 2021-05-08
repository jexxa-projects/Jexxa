# Change Log
All notable changes to this project will be documented in this file.
 
The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## \[3.0.2] - 2021-05-08
### Fixed
-   Fixed fast fail approach in JexxaMain::bindToAnnotation. Now, this method directly fails if any inbound port cannot be created.

### Added
-   New Tutorial [BookstoreJ16](https://github.com/repplix/Jexxa/blob/master/tutorials/BookStoreJ16/README.md) which shows how to use Java records with Jexxa

## \[3.0.1] - 2021-04-18
### Fixed
-   `JSONManager`: 
    -   Bugfix for Java 16+: Added default serializer/deserializer for Java8-time classes, because they are strong encapsulated since Java 16. The added serializer/deserializer avoid an `IllegalAccessException` when they are serialized to/from Json.
-   Fixed issues from static code analysis tool codacy 
-   Updated dependencies on patch level 

## \[3.0.0] - 2021-04-03
### Changed
-   Introduced new package Jexxa-Web which includes all web specific plugins such as `RESTfulRPCAdapter`. See [reference guide](https://repplix.github.io/Jexxa/jexxa_reference.html#_jexxa_modules) for more information. If your application uses this adapter, you have now to add dependency `jexxa-web`. See tutorial [HelloJexxa](https://github.com/repplix/Jexxa/blob/master/tutorials/HelloJexxa/README.md) for an example.  
-   Removed all methods and constants that were declared deprecated in 2.x

### Fixed
-   `RESTfulRPCAdapter`: 
    -   OpenAPI plugin uses global JSONManager for creating prototypes so that specific type converter can be registered    
    
## \[2.8.1] - 2021-02-27
### Fixed
-   Updated JaCoCo plugin so that tests run under Java 15
-   Corrected suffixes of integration tests so that maven option -DskipITs works correct 

## \[2.8.0] - 2021-02-03
### Added
-   `RESTfulRPCAdapter`:
    -   Added support for providing static web pages. See tutorial [HelloJexxa](https://github.com/repplix/Jexxa/blob/master/tutorials/HelloJexxa/README.md)
    -   Write occurred exceptions to Logger  
    
-   JDBC Driven Adapter Strategy:
    -   Added typesafe SQL Builder for `JDBCQuery` and `JDBCCommand`
    -   Using SQL Builder also prevents any SQL injection issues

### Fixed
-   Corrected handling of Java8 Date API so that a date is handled as a string conform to ISO 8601. See [here](https://repplix.github.io/Jexxa/jexxa_reference.html#_json_representation_of_date) for more information. Affected adapter are: 
    -   `JDBCKeyValueRepository`   
    -   `JMSSender`
    -   `JSONMessageListener`:
    
## \[2.7.3] - 2021-01-22
### Fixed
-   JDBCQuery:
    -   Patch for correctly handling SQL `Timestamp` under Windows

## \[2.7.2] - 2021-01-07
### Fixed
-  JMS Listener: 
    - Make abstract method public so that IDE makes an implementation of these abstract methods is automatically made public by an IDE 

## \[2.7.1] - 2020-12-30
### Fixed
-   JDBCQuery: 
    -   Fixed API so that returned streams obviously can include `null` values by returning a `Stream<Optional<T>>`.
    -   Completed API with new methods `isPresent` and `isEmpty` to check if queries return a result.

## \[2.7.0] - 2020-12-29

### Added
-   JDBCRepository:
    -   Added abstract base class `JDBCRepository` for implementing specific repository
    -   Added wrapper classes `JDBCQuery` and `JDBCCommand` for reading and executing commands using Java streams.

-   New Tutorial [TimeService - Flow Of Control](https://github.com/repplix/Jexxa/blob/master/tutorials/TimeService/README-FlowOfControl.md)

### Changed
-   Updated dependencies

-   JDBCKeyValueRepository:
    -   Inherits from JDBCRepository
    -   Changed implementation to `JDBCQuery` and `JDBCCommand`
    
## \[2.6.2] - 2020-12-23
### Fixed
-   OpenAPI-Support in `RESTfulRPCAdapter`:
    -   Corrected schema creation of data types including Java8 date/time types

### Added 
-   New Tutorial [TimeService - Flow Of Control](https://github.com/repplix/Jexxa/blob/master/tutorials/TimeService/README-FlowOfControl.md)

### Changed
-   Updated dependencies on minor level

## \[2.6.1] - 2020-12-12
### Fixed
-   `JDBCKeyValueRepository`: Fixed reconnection on lost JDBC connection for example if database closes connection after some time
-   `JMSAdapter`: Fixes reconnection if entire Session object becomes invalid. Typically, this should only happen if you restart your messaging system.   

### Changed
-   Updated dependencies

## \[2.6.0] - 2020-12-09  
### Added
-   JMSAdapter:
    -   Added default implementations for JMS listener which perform JSON deserialization
    -   Please refer to the tutorials [TimeService](https://github.com/repplix/Jexxa/tree/master/tutorials/TimeService) how to use them.  
    
-   DrivenAdapterStrategies:
    -   Set methods getInstance() of all Manager singletons to deprecate
    -   Added static methods to get/set strategie instances
    -   Updated all tutorials to use these new methods    

### Changed
-   Updated dependencies

## \[2.5.3] - 2020-11-28  

### Fixed
-   RESTfulRPCAdapter: 
    -   Corrected handling of Java8 Date API so that a date is handled as a string which is conform to ISO 8601
    -   Added tests that show how to use this from a client side. See [here](https://github.com/repplix/Jexxa/blob/master/jexxa-core/src/test/java/io/jexxa/infrastructure/drivingadapter/rest/RESTfulRPCJava8DateTimeTest.java) for more information.       
    
### Changed
-   Updated dependencies 

## \[2.5.2] - 2020-11-11  
### Fixed
-   RESTfulRPCAdapter: 
    -   [Issue #7](https://github.com/repplix/Jexxa/issues/7) Stack trace is now included when serializing an Exception  

### Changed
-   Updated [Architecture of Jexxa](https://repplix.github.io/Jexxa/jexxa_architecture.html):
    -   Added Section about General Design Decision. 

-   Updated dependencies on minor level 

## \[2.5.1] - 2020-10-25  
### Fixed

-   OpenAPI-Support in `RESTfulRPCAdapter`: 
    -   Array-types are now correctly handled as method attributes
    -   Corrected handling of abstract and interface parameters. For these parameters no example object can be set.           
    -   Set application type to `application/json` in all cases
    
-   `RESTfulRPCAdapter`: 
    -   Static methods are no longer exposed
    -   Only a single instance of a `RESTfulRPCAdapter` per Properties can be created 
    -   Fixed error message so that correct host and port is stated in exception if binding fails
 
-   `JMXAdapter`:       
    -   Static methods are no longer exposed

-   `JexxaMain`:
    -   Corrected validation of port-adapters to ensure that binding between a generic driving adapter and a port adapter causes an exception.

### Added

-   Added [tutorial](tutorials/BookStoreJ/README-OPENAPI.md) explaining how to enable and use OpenAPI support.          
    
## \[2.5.0] - 2020-10-21
### Added
-   `RESTfulRPCAdapter`: Added OpenAPI support (see [jexxa-application.properties](https://github.com/repplix/Jexxa/blob/master/jexxa-core/src/main/resources/jexxa-application.properties) for more information).  
-   `JexxaMain`: Improved fluent API in `JexxaMain` with `conditionalBind()` which performs a binding only if a condition statement evaluates to true. See tutorial [`TimeService`](https://github.com/repplix/Jexxa/tree/master/tutorials/TimeService) for example.
-   Added a [reference guide](https://repplix.github.io/Jexxa/jexxa_reference.html).   
  
### Fixed
-   `IMDBRepository`: When reset all IMDBRepositories, the internal reference to specific map is also reset. This allows reusing references to IRepository instances.  

-   `RESTfulRPCAdapter`: Public static methods are no longer exposed.  

### Changed
-   Updated dependencies

## \[2.4.2] - 2020-10-08
### Fixed
-   `RESTFullRPCAdapter`: Fixed serialization with objects containing private fields without public getter 
-   Tutorial TimeService: Corrected maven shade plugin filters so that active MQ works as expected. 

### Changed
-   RESTFullRPCAdapter: Changed serialization strategy from Jackson to Gson 
-   Updated dependencies

## \[2.4.1] - 2020-10-03
### Fixed
-   Corrected artifact-id of module **jexxa-test** to lower cases. Changed package to jexxatest to avoid conflicts with main package jexxa(.test)  

### Changed
-   Improved javadoc
-   Updated dependencies on minor level 

## \[2.4.0] - 2020-09-27
### Added
-   New driven adapter strategy `MessageLogger` which writes messages to a logger
-   New module `jexxa-test` which simplifies writing unit tests. `jexxa-test` automatically provides stubs for application specific driven adapters as soon as they use Jexxa's drivenadapter strategies. See tutorial [`BookStore`](https://github.com/repplix/Jexxa/tree/master/tutorials/BookStore) for example.  

### Changed
-   Updated dependencies  

## \[2.3.2] - 2020-09-23
### Fixed
-   `JMSAdapter`: Correctly cleanup internal data structure in case of reconnect. This avoids registering objects multiple 
times in case of reconnect.

## \[2.3.1] - 2020-09-20
### Fixed
-   JexxaMain::getInstanceOfPort() now also creates outbound ports correctly    

### Changed  
-   Updated dependencies
-   Improved error message in case of missing adapters 

## \[2.3.0] - 2020-08-23
### Added
-   [Issue #6](https://github.com/repplix/Jexxa/issues/6) Added HTTPS support in RESTfulRPCAdapter (see [jexxa-application.properties](https://github.com/repplix/Jexxa/blob/master/jexxa-core/src/main/resources/jexxa-application.properties) for more information).                                             
### Fixed
-   [Issue #5](https://github.com/repplix/Jexxa/issues/5) Avoid warn a message "Uncaught Exception Handler already set" with JUnit 
-   Fixed Issue with including correct jexxa-application.properties in tutorials jar with all dependencies. Switched from jar-plugin to maven-shade plugin to define correct jexxa-application.properties.   

### Changed
-   Removed deprecated Jexxa's old JMS API integration 

-   Updated dependencies   

## \[2.2.1] - 2020-08-16
### Added                                       
-   Annotation @CheckReturnValue to fluent API for better IDE support 
      
### Fixed
-   Fixed fast fail approach in JexxaMain::start. Now, this method directly fails if any driving adapter throws an exception during its start method.  
-   Correctly set package which is scanned for inbound ports in tutorial BookStoreJ 

### Changed
-   Updated dependencies
-   Package structure of utils   

## \[2.2.0] - 2020-07-25
### Added                                       
-   Added new fluent API for sending JMS messages  

-   Added MessageSenderManager to configure default message strategy for the application
      
### Fixed
-   JexxaMain::getInstanceOfPort validates port conventions to throw a meaningful exception 

### Changed
-   Jexxa's Interface for integrating JMS API is declared as deprecated

## \[2.1.1] - 2020-07-18
### Added                                       
-   Added tutorials to show usage of Jexxa      

### Fixed
-   Fixed json template for base types in JMX parameter info     

### Changed
-   Updated dependencies 

## \[2.1.0] - 2020-07-07
### Added
-   Added automatic reconnection mechanism for JMS Listener in case of a broker exception

-   Extended `RepositoryManager` for persistence strategies to define strategies on an aggregate level

-   Added tutorials to show usage of Jexxa      

### Changed
-   Updated dependencies 

## \[2.0.0] - 2020-06-01
### Added
-   Added global exception handler in JexxaMain for getting better results of startup errors 

-   Improved the documentation of Jexxa (see [Architecture of Jexxa](https://repplix.github.io/Jexxa/jexxa.html)) 

### Changed
-   Split Jexxa into Jexxa-Core and Jexxa-Adapter-API projects in order to avoid direct dependencies to new driving adapter 
-   Moved CompositeDrivingAdapter as inner class of JexxaMain because it is only used there   

-   Naming convention for generic driving adapter: 
    -   Class name of the adapter itself ends with `Adapter` 
    -   Class name of the convention ends with `Convention`  

-   Naming convention for specific driving adapter: 
    -   Class name of the adapter itself ends with `Adapter` 
    -   Class name of the configuration ends with `Configuration`  

-   Updated dependencies 

## \[1.4.2] - 2020-05-22

### Changed
-   Documentation about Jexxa in doc/jexxa.adoc
-   Properties for JDBC databases (see [jexxa-application.properties](https://github.com/repplix/Jexxa/blob/master/jexxa-core/src/main/resources/jexxa-application.properties))

### Fixed
-   Checking Jexxa's conventions for ports and adapters with a fail fast approach
 
## \[1.4.1] - 2020-05-17

### Added
-   Thread-safety for accessing ports by multiple driving adapters in parallel   

### Changed
-   Documentation about Jexxa in doc/jexxa.adoc

### Fixed
-   Some code cleanup based on static code analysis 
                             
## \[1.4] - 2020-05-01
 
### Added
-   Changelog
 
### Changed
-   Changed groupID from io.ddd.jexxa into io.jexxa 
-   Code cleanup found by static code analysis with Teamscale
-   Updated README

### Fixed
-   JMXAdapter: Added context name to BeanInfo instead of "Hello Jexxa"

## \[1.3] - 2020-04-19
 
### Added
-   Fluent API for application developer to bind adapter to ports 
-   Dependency injection of driven adapter into application core (without using annotations in application core)   
-   Driven adapter for JDBC, IMDB, and JMS
-   Driving adapter for JMX
   
### Changed
-   Updated all tests to Junit5
-   Completely rewrote driving adapter for RESTfulRPC
 
## \[1.2] - 2020-03-18
 
### Fixed
-   Javadoc issues during release build  
 
## \[1.1] - 2020-03-18
 
### Fixed
-   Maven build and release on Github
   
## \[1.0] - 2020-03-18
 
### Added
-   Initial version of checking dependencies of the application core 
-   Initial version of driving adapter for RESTfulRPC  