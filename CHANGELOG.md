# Change Log
All notable changes to this project will be documented in this file.
 
The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## \[2.x.x] - yyyy-mm-dd
### Added                                       
-   Added new fluent API for sending JMS messages  
      
### Fixed

### Changed
-   Old JMS API is declared as deprecated

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
