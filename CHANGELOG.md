# Change Log
All notable changes to this project will be documented in this file.
 
The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## \[7.0.0] - 2024-01-04
### Fixed
- Updated dependencies

## \[7.0.0] - 2023-12-16
## Changed
- Jexxa now utilises [Jexxa-Adapters](https://github.com/jexxa-projects/JexxaAdapters) as underlying technology stack. This enables to focus Jexxa more on its API and simplifies reusing technology stacks in other projects.
- For jexxa based applications, you typically only need to adjust the imports as described in the migration notes below.  

### Migration notes from 6.x.x -> 7.x.x!
To simplify migration, you have to adjust the following imports: 
- Static imports
    - `io.jexxa.infrastructure.MessageSenderManager` -> `io.jexxa.common.drivenadapter.messaging.MessageSenderManager`
    - `io.jexxa.infrastructure.ObjectStoreManager` -> `io.jexxa.common.drivenadapter.persistence.ObjectStoreManager`
    - `io.jexxa.infrastructure.RepositoryManager` -> `io.jexxa.common.drivenadapter.persistence.RepositoryManager`
- Package structure
    - `io.jexxa.infrastructure` -> `io.jexxa.common.drivenadapter`
    - `io.jexxa.drivingadapter.messaging` -> `io.jexxa.common.drivingadapter.messaging.jms`
    - `io.jexxa.drivingadapter.scheduler` -> `io.jexxa.common.drivingadapter.scheduler`
    - `io.jexxa.common.wrapper` -> `io.jexxa.common.facade`
    - `io.jexxa.common.drivenadapter.healthcheck` -> `io.jexxa.common.healthcheck`


## \[6.2.4] - 2023-12-15
### Fixed
- Updated dependencies
- Log-messages in architecture-tests so that the name of the failed method is added 

## \[6.2.3] - 2023-11-12
### Fixed
- Updated dependencies

## \[6.2.2] - 2023-10-26
### Fixed
- Updated dependencies
- Fixed issues reported by CodeQL

## \[6.2.1] - 2023-10-05
### Fixed
- Updated dependencies

## \[6.2.0] - 2023-09-17
### Added 
- Jexxa-Web: 
  - Possibility to pass method parameters as a single JSONObject. The parameters of the method are added with key `arg0` to `argN`. See [description for HTML `Request: Method attributes`](https://jexxa-projects.github.io/Jexxa/jexxa_reference.html#_conventions_of_restfulrpcadapter) for more information. 

### Fixed
- Jexxa-Core:
  - Serialization of complex keys in Map 
- Jexxa-Web:
    - Enums are not correctly described in OpenAPI specification 
    - Methods with multiple parameters are now described with key `arg0` to `argN`
  
## \[6.1.6] - 2023-09-05
### Fixed
- Jexxa-Core:
    - Outbound-Ports are now correctly created when used as `bootstrap`service.  

- Updated dependencies

## \[6.1.5] - 2023-08-18
### Fixed
- Jexxa-Test: 
  - Architecture tests for Aggregates no longer fail if no Aggregate is included in the project  

- Updated dependencies

## \[6.1.4] - 2023-07-29
### Fixed
- Jexxa-Core: 
  - DrivingAdapter `Scheduler` now logs and catches exceptions in scheduled methods so that they continue to be called
  
- Jexxa-Test: 
  - Architecture tests now fail if an Aggregate is not annotated with @AggregateID
  - Architecture tests now fail if an Application-, or DomainService return generics including Aggregates
  
- Updated dependencies

## \[6.1.3] - 2023-07-07
### Fixed
- Updated dependencies

## \[6.1.2] - 2023-06-20
### Fixed
- Updated dependencies

## \[6.1.1] - 2023-06-08
### Fixed
- [ #252 ](https://github.com/jexxa-projects/Jexxa/issues/252) RESTFulRPCAdapter does not deserialize generics of records correctly 
- Updated dependencies

## \[6.1.0] - 2023-06-01
### Added
-   `RESTFullRPCAdapter`: Automatically generated OpenAPI specification now sets the operationID as description and summary as requested in #244. In addition, the class name of a method is set as tag to support grouping of methods

### Fixed
- Updated dependencies


## \[6.0.8] - 2023-05-28
### Fixed
- Updated dependencies

### Fixed
- In debug mode, complete stack trace is written to logger  
- Clarified error message in case no public constructor of factory method is available 
- Updated dependencies 

## \[6.0.6] - 2023-05-01
### Fixed
- JexxaTest: Jexxa's dependency injection and manual injection in unit tests can now be used in mixed mode, and JexxaTest correctly cleans up `IMDRepository` when used.  
- Updated dependencies

## \[6.0.5] - 2023-04-19
### Fixed
- Fixed IMDB Repository implementation so that changes on aggregates are only stores if `update()` is called.
- Updated dependencies

## \[6.0.4] - 2023-04-09
### Fixed
- Improved warning and error handling and messaging.
- Updated dependencies

## \[6.0.3] - 2023-03-21
### Fixed
- Fixed cleanup of expired messages in IdempotentListener under a heavy message load (one message each msec).
- Updated dependencies 

## \[6.0.2] - 2023-03-08
### Fixed
- Fixed Dependency Injection for port adapters that get additional Properties as second argument, such as IdempotentListener 

## \[6.0.1] - 2023-03-08
### Fixed 
- [#208](https://github.com/jexxa-projects/Jexxa/issues/208)Architecture tests know handles anonymous inner classes correctly 
- Updated dependencies

## \[6.0.0] - 2023-03-05
### Changed—Important Information!

- With this major release, Jexxa focuses more on typical microservices patterns. To better highlight them, the following changes have been made to the package structures:
  - `core`: Provides the main entry point for a Jexxa application
  - `drivingadapter`: Provides ready-to-use driving adapter to receive and forward incoming requests to the application core 
  - `infrastructure`: Replaces old `drivenadapterstrategy` and provides implementation of typical application infrastructure patterns that simplify the implementation of driven adapters
  - `common`: Provides wrapper to standard java libraries used by Jexxa. The main focus is to simplify and reduce these APIs to the requirements of Jexxa. This package is a candidate to be externalized in future major releases.     
 
- `TransactionalOutboxSender` is now used by default for sending messages.   

- Renamed `JexxaLogger` &rarr; `SLF4JLogger` to clarify its responsibility.
- Renamed `Monitors` &rarr; `HealthIndicators` to clarify its responsibility. 
- Renamed `TimerMonitor` &rarr; `TimeoutIndicator` to clarify its responsibility.

- Changed HTTP-properties to meet declaration convention: 
  - `io.jexxa.rest.open_api_path` &rarr; `io.jexxa.rest.openapi.path`
  - `io.jexxa.rest.static_files_root` &rarr; `io.jexxa.rest.static.files.root`
  - `io.jexxa.rest.static_files_external` &rarr; `io.jexxa.rest.static.files.external` 

- Changed HTTPS-properties to meet declaration convention:
  - `io.jexxa.rest.https_port` &rarr; `io.jexxa.rest.https.port`
  - `io.jexxa.rest.keystore_password` &rarr; `io.jexxa.rest.keystore.password`
  - `io.jexxa.rest.file.keystore_password` &rarr; `io.jexxa.rest.keystore.file.password`
  - `io.jexxa.rest.keystore` &rarr; `io.jexxa.rest.keystore.location`

- Removed all deprecated declarations:
  - Jexxa-Adapter-API: Removed `InvocationHandler` which is named now `JexxaInvocationHandler` 
  - Jexxa-Core: Removed deprecated `JMXAdapter` and deprecated static properties
  - Jexxa-Web: Removed deprecated static properties 

### Added
- `IdempotentListener`: Discards duplicate messages based on a unique key in message header
- `ObjectStore`: creates a combined index for all values that can be searched for.   

### Fixed
- Updated dependencies

## \[5.7.1] - 2023-02-24
### Fixed
- Updated dependencies
- `TransactionalOutboxSender` initiates sending of messages directly after storing messages so that no additional delay occurs 

## \[5.7.0] - 2023-02-19
### Added
- [ #186 ](https://github.com/jexxa-projects/Jexxa/issues/186) Jexxa-Core: Added support for a transactional outbox pattern for sending JMS messages, called `TransactionalOutboxSender`. By default, the old `JMSSender` is still used. 

### Changed
- Jexxa-Core: Declared driving adapter `JMXAdapter` as deprecated since it is no longer used in any project.

### Fixed
- Updated dependencies

## \[5.6.2] - 2023-02-02

### Fixed
- Updated dependencies

## \[5.6.1] - 2023-01-26

### Fixed
- [ #185 ](https://github.com/jexxa-projects/Jexxa/issues/185) Architecture test PortsAndAdapters fails on valid setup
- Interceptors work correctly with methods using abstract parameters
- Updated dependencies

## \[5.6.0] - 2023-01-08

### Added
- Jexxa-Core: Improved API for bootstrapping classes in main() that need no initialization method. See [here](https://github.com/jexxa-projects/JexxaTemplate) for more information. 
  
- Jexxa-Core: JMSConfiguration can be provided by a method of the message listener. This allows implementing configurable message listener. See [here](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-core/src/test/java/io/jexxa/infrastructure/utils/messaging/ConfigurableListener.java) for an example.  

- Jexxa-Test: Provided convenience classes for implementing integration tests. For detailed information and example, look [here](https://github.com/jexxa-projects/JexxaTemplate).

### Changed
- Jexxa-Core: static definitions of JMS properties are now provided via `JexxaJMSProperties` for unification reason. The old properties provided in `JMSAdapter` are declared deprecated.

### Fixed
- Updated dependencies

## \[5.5.4] - 2022-12-19
### Fixed
- Updated dependencies

## \[5.5.3] - 2022-12-11
### Fixed
- Updated dependencies

## \[5.5.2] - 2022-12-03
### Fixed
- Removed unnecessary transitive dependency snakeyaml due to security issue [CVE-2022-41854](https://devhub.checkmarx.com/cve-details/CVE-2022-41854/)
- Updated dependencies

## \[5.5.1] - 2022-11-23
### Fixed
- Generation of OpenAPI documentation for arrays of base types, such as String[] 
- Updated dependencies
 
## \[5.5.0] - 2022-11-20
### Added
- Jexxa-Core: Provide possibility to configure `user.timezone` via jexxa-application.properties value `io.jexxa.user.timezone`. See [here](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties) for more information

### Changed
- [Issue #142](https://github.com/jexxa-projects/Jexxa/issues/140) Jexxa-Web: Updated from javalin4 to javalin5. This required reimplementing OpenAPI functionality because javalin5 no longer supports a Java-DSL to provide OpenAPI information. Even though the public API of Jexxa-Web is not changed we decided to increment minor version due to the re-implementation of OpenAPI 

### Fixed
- Updated dependencies


## \[5.4.0] - 2022-11-05
### Added
- Jexxa-Core: Added new driving adapter `Scheduler` which allows to call methods on a fixed rate or fixed delay as described in [issue #140](https://github.com/jexxa-projects/Jexxa/issues/140). See [here](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties) for more some examples hwo to use it. 

### Fixed
- Updated dependencies


## \[5.3.1] - 2022-10-27
### Fixed
- Explicitly define one latest gson version as dependency for jexxa-web. This ensures that maven will use this version within your application because it is the shortest path as a transitive dependency. In case your application uses an older gson version (also as transitive dependency), you could get an IllegalAccessException because deserialization of a Java record fails.  

- Updated dependencies

## \[5.3.0] - 2022-10-26
### Added
- Jexxa-Test: Possibility to define one stubs for your tests

### Changed
- Jexxa now uses (de)serialization methods for Java records provided in the latest GSon library

### Fixed
- Updated dependencies 

## \[5.2.3] - 2022-10-05
### Added
- Updated dependencies with security issues

## \[5.2.2] - 2022-10-03
### Fixed
- Improved log messages in case of an error during startup
- Updated dependencies 

## \[5.2.1] - 2022-09-11
* [Fix] JMSAdapter: Specified name for a shared connection was not passed to JMS broker, instead the message selector 
  was passed. This fix ensures that all parameters of a shared connection are correctly handled 

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
  - Added static method `getJexxaTest, which also instantiates JexxaMain. This ensures that all driven adapters are correctly configured before any method on JexxaMain is called  
  - Added rules to validate the architecture of your application. 
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
### Changed—Important Information!
- With this major release, Jexxa supports only Java versions >=17.
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
    - jexxa-application.properties: Is automatically loaded and should include your production settings
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
- Minor and patch numbers of jexxa-adapter-api are now kept in sync with other jexxa-versions. This should avoid confusion about compatible version numbers 

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
### Changed—Important Information!
- Jexxa-Core: Major changes to the persistence layer, which prevent downgrading applications to older Jexxa versions
    - `JDBCKeyValueRepository` and `JDBCObjectStore` use now `JSONB` format if a Postgres DB is used. Existing database are automatically converted to `JSONB`
    - `JDBCKeyValueRepository` and `JDBCObjectStore` use now column name `REGISTRY_KEY` and `REGISTRY_VALUE` instead `KEY` and `VALUE`. This avoids conflicts with reserved SQL statements. Existing database are automatically updated.
    - `Repository` related interfaces and classes were moved from `...persistence` into sub-package `...persistence.repository` -> You have to update your imports in your application

- Jexxa-Core: Removed all components declared as deprecated

- Updated dependencies

### Added
- Jexxa-Core: Added possibility to load Properties file defined by `JEXXA_CONFIG_IMPORT`

- Jexxa-Core/Jexxa-Web: Added files that provide all properties used by Jexxa: 
  - [JexxaCoreProperties](./jexxa-core/src/main/java/io/jexxa/properties/JexxaCoreProperties.java)
  - [JexxaJDBCProperties](./jexxa-core/src/main/java/io/jexxa/properties/JexxaJDBCProperties.java)
  - [JexxaWebProperties](./jexxa-web/src/main/java/io/jexxa/drivingadapter/rest/JexxaWebProperties.java)

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

- Jexxa-Core/web: Added new properties to automatically load version information of applications from a build system.
  See [jexxa-application.properties](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties) for more information.

- Jexxa-Core: JSon serializer allows now reading a Type in addition to Clazz and reading from a `Reader`

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
-   JexxaTest: Corrected default configuration for ObjectStore so that IMDBObjectStore is used for unit tests.

### Changed
-   Updated dependencies

## \[3.1.0] - 2021-08-14
### Added 
-   Added `ObjectStore` which provides sophisticated API for querying managed objects. See tutorial [Contract Management](https://github.com/jexxa-projects/Jexxa/blob/master/tutorials/ContractManagement/README.md)  

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
-   JexxaMain: If a specific driving adapter is bound to a port adapter, Jexxa ensures that the specific driving adapter is instantiated only once. Without this fix, it could happen that a specific driving adapter is instantiated multiple times. In case the specific driving adapter uses resources such as a port, this led to an exception.

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
-   New Tutorial [`BookstoreJ16`](https://github.com/jexxa-projects/JexxaTutorials/tree/main/BookStoreJ16/README.md) which shows how to use Java records with Jexxa

## \[3.0.1] - 2021-04-18
### Fixed
-   `JSONManager`: 
    -   Bugfix for Java 16+: Added default serializer/deserializer for Java8-time classes, because they are strongly encapsulated since Java 16. The added serializer/deserializer avoid an `IllegalAccessException` when they are serialized to/from Json.
-   Fixed issues from static code analysis tool codacy 
-   Updated dependencies on patch level 

## \[3.0.0] - 2021-04-03
### Changed
-   Introduced new package Jexxa-Web, which includes all web-specific plugins, such as `RESTfulRPCAdapter`. See [reference guide](https://jexxa-projects.github.io/Jexxa/jexxa_reference.html#_jexxa_modules) for more information. If your application uses this adapter, you have now to add dependency `jexxa-web`. See tutorial [HelloJexxa](https://github.com/jexxa-projects/JexxaTutorials/tree/main/HelloJexxa/README.md) for an example.  
-   Removed all methods and constants declared deprecated in 2.x

### Fixed
-   `RESTfulRPCAdapter`: 
    -   OpenAPI plugin uses global JSONManager for creating prototypes so that specific type IConverter can be registered    
    
## \[2.8.1] - 2021-02-27
### Fixed
-   Updated JaCoCo plugin so that tests run under Java 15
-   Corrected suffixes of integration tests so that maven option `-DskipITs` works correct 

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

-   New Tutorial [TimeService—Flow Of Control](https://github.com/jexxa-projects/JexxaTutorials/tree/main/TimeService/README-FlowOfControl.md)

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
-   New Tutorial [TimeService—Flow Of Control](https://github.com/jexxa-projects/JexxaTutorials/tree/main/TimeService/README-FlowOfControl.md)

### Changed
-   Updated dependencies on minor level

## \[2.6.1] - 2020-12-12
### Fixed
-   `JDBCKeyValueRepository`: Fixed reconnection on lost JDBC connection, for example, if a database closes connection after some time
-   `JMSAdapter`: Fixes reconnection if the entire Session object becomes invalid. Typically, this should only happen if you restart your messaging system.   

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
    -   Update all tutorials to use these new methods    

### Changed
-   Updated dependencies

## \[2.5.3] - 2020-11-28  

### Fixed
-   RESTfulRPCAdapter: 
    -   Corrected handling of Java8 Date API so that a date is handled as a string which is conformed to ISO 8601
    -   Added tests that show how to use this from the client side. See [here](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-core/src/test/java/io/jexxa/infrastructure/drivingadapter/rest/RESTfulRPCJava8DateTimeTest.java) for more information.       
    
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
    -   Corrected handling of abstract and interface parameters. For these parameters, no example object can be set.           
    -   Set application type to `application/json` in all cases
    
-   `RESTfulRPCAdapter`: 
    -   Static methods are no longer exposed
    -   Only a single instance of a `RESTfulRPCAdapter` per Properties can be created 
    -   Fixed error message so that the correct host and port is stated in exception if binding fails
 
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
-   `IMDBRepository`: When reset all IMDBRepositories, the internal reference to a specific map is also reset. This allows reusing references to IRepository instances.  

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
-   Corrected artifact-id of module **jexxa-test** to lower cases. Changed package to `jexxatest` to avoid conflicts with main package jexxa(.test)  

### Changed
-   Improved javadoc
-   Updated dependencies on minor level 

## \[2.4.0] - 2020-09-27
### Added
-   New driven adapter strategy `MessageLogger` which writes messages to a logger
-   New module `jexxa-test` which simplifies writing unit tests. `jexxa-test` automatically provides stubs for application-specific driven adapters as soon as they use Jexxa's drivenadapter strategies. See tutorial [`BookStore`](https://github.com/jexxa-projects/JexxaTutorials/blob/main/BookStore) for example.  

### Changed
-   Updated dependencies  

## \[2.3.2] - 2020-09-23
### Fixed
-   `JMSAdapter`: Correctly cleanup internal data structure in case of reconnecting. This avoids registering objects multiple 
times in case of reconnecting.

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
-   [Issue #5](https://github.com/jexxa-projects/Jexxa/issues/5) Avoid a warning message "Uncaught Exception Handler already set" with JUnit 
-   Fixed Issue with including correct jexxa-application.properties in tutorial jar with all dependencies. Switched from jar-plugin to maven-shade plugin to define correct jexxa-application.properties.   

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
-   Split Jexxa into Jexxa-Core and Jexxa-Adapter-API projects to avoid direct dependencies to new driving adapter 
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
