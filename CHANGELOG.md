# Change Log
All notable changes to this project will be documented in this file.
 
The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## \[5.2.0] - 2022-09-11
### Added
- Jexxa-Core: Possibility to define JMS >=2.0 parameter for listener  

## \[5.1.2] - 2022-09-10
### Fixed
- JSon Deserialization of java-records including generic types like a list. 

## \[5.1.1] - 2022-09-07
### Fixed
- Updated dependencies

## \[5.1.0] - 2022-08-15
### Added
- Jexxa-Test: 
  - Added static method getJexxaTest which also instantiates JexxaMain. This ensures that all driven adapter are correctly configured before any method on JexxaMain is called  
  - Added rules to validate architecture of your application. 
  - See [JexxaTemplate](https://github.com/jexxa-projects/JexxaTemplate) for more information

### Fixed
- Jexxa-Core: If deserialization of record fails, the exception from the canonical constructor is forwarded (if available). 

## \[5.0.2] - 2022-08-05
### Fixed
- Updated dependencies

## \[5.0.1] - 2022-07-09
### Fixed
- MessageLogger shows also message properties
- Updated dependencies


## \[5.0.0] - 2022-06-17
### Changed - Important Information!
- With this major release Jexxa supports only Java versions >=17.
- See [JexxaTemplate](https://github.com/jexxa-projects/JexxaTemplate) or [JexxaTutorials](https://github.com/jexxa-projects/JexxaTutorials) for examples.

- Jexxa-Core:
  - Removed all deprecated methods: 
    - Constructor `JexxaMain(String contextName, Properties applicationProperties)` was removed. Instead use  `JexxaMain(Class<?> context, Properties applicationProperties)`
  - Changed getter-API for exposed classes such as `BoundedContext` so that they do not use prefix `get` as introduced with naming conventions of Java records.

- JexxaMain: 
  - Streamlined main: Method `addDDDPackages` is invoked by default for the given context. 

- Messaging Strategy:
  - According to a Repository, you can now define the used messaging strategy for each interface. Therefore, you have to pass the implemented interface to the MessageSenderManager when querying the strategy.

- Properties: 
  - Driven-Adapter-Strategies can now be defined in the properties files. 
  - In general, Jexxa applications have up to three different configurations (at max): 
    - jexxa-application.properties: Is automatically load and should include your production settings
    - jexxa-test.properties: Overwrites all production settings for local testing
    - jexxa-local.properties: Overwrites all production settings so that the application can run without an infrastructure. This is only recommended for demo purpose or rapid prototyping. 
    - Please refer to [reference guide](https://jexxa-projects.github.io/Jexxa/jexxa_reference.html#_application_configuration) for more information.

### Added
- Jexxa-Core: 
    - Jexxa includes full support for Java `record` (de-)serialization by default. So no explicit serializer needs to be defined within the infrastructure of your application.
    
- JexxaMain:
  - Added convenience method `run()` that invokes `start()`, `waitUntilShutdown()`, and `stop()`
  - Added method `logUnhealthyDiagnostics` to log unhealthy diagnostics for registered Monitors

### Fixed
- Updated dependencies

## \[4.1.8] - 2022-05-21
### Fixed
- Updated dependencies
- Excluded transitive dependencies with security issues


## \[4.1.7] - 2022-05-21
### Fixed
- Updated dependencies

## \[4.1.6] - 2022-05-12
### Fixed
- Updated dependencies

## \[4.1.5] - 2022-04-26
### Changed 
- Minor and patch numbers of jexxa-adapter-api are now kept in sync with other jexxa-versions. This should avoid confusions about compatible version numbers 

### Fixed
- Updated dependencies

## \[4.1.4] - 2022-04-15
### Added
- Official support for JDK 18 

### Fixed
- Updated dependencies

## \[4.1.3] - 2022-03-19
### Fixed
- Updated dependencies

## \[4.1.2] - 2022-02-27
### Fixed
- Fixed json (de-)serialization of exceptions for Java >=16. The new implementation does not collide with the sealed classes/packages in `java.lang` and `java.io`.  
- Updated dependencies

## \[4.1.1] - 2022-02-20
### Changed
- Moved tutorials into new repository [JexxaTutorials](https://github.com/jexxa-projects/JexxaTutorials)

### Fixed
- Updated dependencies
- Corrected/improved error messages during starting Jexxa

## \[4.1.0] - 2022-02-02
### Added 
- Jexxa-Core: 
  - Monitor classes to observe driving adapter
  - Extended BoundedContext to query health status based on HealthChecks  
  
### Changed 
- Updated dependencies

### Fixed
- Corrected implementation of addDDDPackages so that package for driven adapter is included
- Corrected handling of Port-Adapter as singleton instances 

## \[4.0.0] - 2022-01-28
### Changed - Important Information!
- Jexxa-Core: Major changes to the persistence layer, which prevent downgrading applications to older Jexxa versions
    - `JDBCKeyValueRepository` and `JDBCObjectStore` use now `JSONB` format if a Postgres DB is used. Existing database are automatically converted to `JSONB`
    - `JDBCKeyValueRepository` and `JDBCObjectStore` use now column name `REGISTRY_KEY` and `REGISTRY_VALUE` instead `KEY` and `VALUE`. This avoids conflicts with reserved SQL statements. Existing database are automatically updated.
    - `Repository` related interfaces and classes were moved from `...persistence` into sub-package `...persistence.repository` -> You have to update your imports in your application

- Jexxa-Core: Removed all components declared as deprecated

- Updated dependencies

### Added
- Jexxa-Core: Added possibility to load Properties file defined by `JEXXA_CONFIG_IMPORT`

- Jexxa-Core/Jexxa-Web: Added files that provide all properties used by Jexxa: 
  - [JexxaCoreProperties](./jexxa-core/src/main/java/io/jexxa/utils/properties/JexxaCoreProperties.java)
  - [JexxaJDBCProperties](./jexxa-core/src/main/java/io/jexxa/utils/properties/JexxaJDBCProperties.java)
  - [JexxaWebProperties](./jexxa-web/src/main/java/io/jexxa/infrastructure/drivingadapter/rest/JexxaWebProperties.java)

- Jexxa-Adapter-API: Added possibility to set interceptor between `DrivingAdapters` and `InboundPorts`

## \[3.3.2] - 2022-01-03
### Changed
- Updated dependencies

## \[3.3.1] - 2021-11-26
### Fixed
- Jexxa-Core: Corrected JDBC properties from `io.jexxa.file.username` -> `io.jexxa.jdbc.file.username` 

## \[3.3.0] - 2021-11-26
### Added
- Jexxa-Core/web: Added new properties to read credentials from files.
  See [jexxa-application.properties](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties) for more information.

- Jexxa-Core/web: Added new properties to automatically load version information of applications from build system.
  See [jexxa-application.properties](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties) for more information.

- Jexxa-Core: JSon serializer allows now to read a Type in addition to Clazz and to read from a `Reader`

### Changed
- Reference guide: Updated description of application configuration  
    See [here](https://jexxa-projects.github.io/Jexxa/jexxa_reference.html#_application_configuration) for more information.

- Updated dependencies

## \[3.2.0] - 2021-11-05
### Added
- Jexxa-Web: Added possibility to configure an external path by using property `io.jexxa.rest.static_files_external=true`.
 See [jexxa-application.properties](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties) for more information.   

### Changed
-   Updated dependencies

## \[3.1.4] - 2021-10-30
### Changed
-   Maintenance release: Updated dependencies

## \[3.1.3] - 2021-10-17
### Changed
-   Maintenance release: Updated dependencies
-   Improved error messages if starting the application fails 

## \[3.1.2] - 2021-09-18
### Changed
-   Maintenance release: Updated dependencies

## \[3.1.1] - 2021-08-23
### Fixed
-   JexxaTest: Corrected default configuration for ObjectStore so that IMDBObjectStore is used in case of unit tests.

### Changed
-   Updated dependencies

## \[3.1.0] - 2021-08-14
### Added 
-   Added ObjectStore which provides sophisticated API for querying managed objects. See tutorial [Contract Management](https://github.com/jexxa-projects/Jexxa/blob/master/tutorials/ContractManagement/README.md)  

### Changed
-   Default port for tutorials from 7000 -> 7500 because macOS Monterey uses port 7000 for its control center
-   Updated dependencies 

## \[3.0.6] - 2021-07-13
### Fixed
-   JDBCQuery: Corrected usage of PreparedStatement to correctly close the resource.  

## \[3.0.5] - 2021-07-10
### Fixed
-   JDBCCommand / JDBCQuery: Internal used PreparedStatement is now correctly closed to avoid issues with dangling resources. 
    Note: Even though this issue depends on your database driver, it occurs at least on Oracle databases.
    
### Changed 
-   Updated dependencies on minor and patch level

## \[3.0.4] - 2021-06-18
### Fixed
-   JexxaMain: If a specific driving adapter is bound to a port adapter Jexxa ensured that the specific driving adapter is instantiated only once. Without this fix it could happen that a specific driving adapter is instantiated multiple times. In case the specific driving adapter uses resources such as a port this led to an exception.

### Changed
-   Updated dependencies on patch level

## \[3.0.3] - 2021-06-05
### Fixed
-   Corrected javadoc und README files  

### Changed
-   Updated dependencies on patch level

## \[3.0.2] - 2021-05-08
### Fixed
-   Fixed fast fail approach in JexxaMain::bindToAnnotation. Now, this method directly fails if any inbound port cannot be created.

### Added
-   New Tutorial [BookstoreJ16](https://github.com/jexxa-projects/JexxaTutorials/tree/main/BookStoreJ16/README.md) which shows how to use Java records with Jexxa

## \[3.0.1] - 2021-04-18
### Fixed
-   `JSONManager`: 
    -   Bugfix for Java 16+: Added default serializer/deserializer for Java8-time classes, because they are strong encapsulated since Java 16. The added serializer/deserializer avoid an `IllegalAccessException` when they are serialized to/from Json.
-   Fixed issues from static code analysis tool codacy 
-   Updated dependencies on patch level 

## \[3.0.0] - 2021-04-03
### Changed
-   Introduced new package Jexxa-Web which includes all web specific plugins such as `RESTfulRPCAdapter`. See [reference guide](https://jexxa-projects.github.io/Jexxa/jexxa_reference.html#_jexxa_modules) for more information. If your application uses this adapter, you have now to add dependency `jexxa-web`. See tutorial [HelloJexxa](https://github.com/jexxa-projects/JexxaTutorials/tree/main/HelloJexxa/README.md) for an example.  
-   Removed all methods and constants that were declared deprecated in 2.x

### Fixed
-   `RESTfulRPCAdapter`: 
    -   OpenAPI plugin uses global JSONManager for creating prototypes so that specific type IConverter can be registered    
    
## \[2.8.1] - 2021-02-27
### Fixed
-   Updated JaCoCo plugin so that tests run under Java 15
-   Corrected suffixes of integration tests so that maven option -DskipITs works correct 

## \[2.8.0] - 2021-02-03
### Added
-   `RESTfulRPCAdapter`:
    -   Added support for providing static web pages. See tutorial [HelloJexxa](https://github.com/jexxa-projects/JexxaTutorials/tree/main/HelloJexxa/README.md)
    -   Write occurred exceptions to Logger  
    
-   JDBC Driven Adapter Strategy:
    -   Added typesafe SQL Builder for `JDBCQuery` and `JDBCCommand`
    -   Using SQL Builder also prevents any SQL injection issues

### Fixed
-   Corrected handling of Java8 Date API so that a date is handled as a string conform to ISO 8601. See [here](https://jexxa-projects.github.io/Jexxa/jexxa_reference.html#_json_representation_of_date) for more information. Affected adapter are: 
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

-   New Tutorial [TimeService - Flow Of Control](https://github.com/jexxa-projects/JexxaTutorials/tree/main/TimeService/README-FlowOfControl.md)

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
-   New Tutorial [TimeService - Flow Of Control](https://github.com/jexxa-projects/JexxaTutorials/tree/main/TimeService/README-FlowOfControl.md)

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
    -   Please refer to the tutorials [TimeService](https://github.com/jexxa-projects/JexxaTutorials/tree/main/TimeService) how to use them.  
    
-   DrivenAdapterStrategies:
    -   Set methods getInstance() of all Manager singletons to deprecate
    -   Added static methods to get/set strategie instances
    -   Updated all tutorials to use these new methods    

### Changed
-   Updated dependencies

## \[2.5.3] - 2020-11-28  

### Fixed
-   RESTfulRPCAdapter: 
    -   Corrected handling of Java8 Date API so that a date is handled as a string which is conformed to ISO 8601
    -   Added tests that show how to use this from a client side. See [here](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-core/src/test/java/io/jexxa/infrastructure/drivingadapter/rest/RESTfulRPCJava8DateTimeTest.java) for more information.       
    
### Changed
-   Updated dependencies 

## \[2.5.2] - 2020-11-11  
### Fixed
-   RESTfulRPCAdapter: 
    -   [Issue #7](https://github.com/jexxa-projects/Jexxa/issues/7) Stack trace is now included when serializing an Exception  

### Changed
-   Updated [Architecture of Jexxa](https://jexxa-projects.github.io/Jexxa/jexxa_architecture.html):
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

-   Added [tutorial](https://github.com/jexxa-projects/JexxaTutorials/blob/main/BookStoreJ/README-OPENAPI.md) explaining how to enable and use OpenAPI support.          
    
## \[2.5.0] - 2020-10-21
### Added
-   `RESTfulRPCAdapter`: Added OpenAPI support (see [jexxa-application.properties](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties) for more information).  
-   `JexxaMain`: Improved fluent API in `JexxaMain` with `conditionalBind()` which performs a binding only if a condition statement evaluates to true. See tutorial [`TimeService`](https://github.com/jexxa-projects/JexxaTutorials/tree/main/TimeService) for example.
-   Added a [reference guide](https://jexxa-projects.github.io/Jexxa/jexxa_reference.html).   
  
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
-   New module `jexxa-test` which simplifies writing unit tests. `jexxa-test` automatically provides stubs for application specific driven adapters as soon as they use Jexxa's drivenadapter strategies. See tutorial [`BookStore`](https://github.com/jexxa-projects/JexxaTutorials/blob/main/BookStore) for example.  

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
-   [Issue #6](https://github.com/jexxa-projects/Jexxa/issues/6) Added HTTPS support in RESTfulRPCAdapter (see [jexxa-application.properties](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties) for more information).                                             
### Fixed
-   [Issue #5](https://github.com/jexxa-projects/Jexxa/issues/5) Avoid warn a message "Uncaught Exception Handler already set" with JUnit 
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

-   Improved the documentation of Jexxa (see [Architecture of Jexxa](https://jexxa-projects.github.io/Jexxa/jexxa.html)) 

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
-   Properties for JDBC databases (see [jexxa-application.properties](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties))

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
-   Maven build and release on GitHub
   
## \[1.0] - 2020-03-18
 
### Added
-   Initial version of checking dependencies of the application core 
-   Initial version of driving adapter for RESTfulRPC  
