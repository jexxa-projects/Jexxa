# Change Log
All notable changes to this project will be documented in this file.
 
The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## \[2.x.x] - yyyy-mm-dd
### Added
-   `JexxaMain`: Improved fluent API in `JexxaMain` whith `conditionalBind()` which performs a binding only if a condition statement evaluates to true. See tutorial [`TimeService`](https://github.com/repplix/Jexxa/tree/master/tutorials/TimeService) for example.  
  
### Fixed
-   `IMDBRepository`: When reset all IMDBRepositories, the internal reference to specific map is also reset. This allows reusing references to IRepository instances.  

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