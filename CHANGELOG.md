# Change Log
All notable changes to this project will be documented in this file.
 
The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## \[2.0.0-UNRELEASED] - yyyy-mm-dd
### Added

### Changed
-   Split Jexxa into Jexxa-Core and Jexxa-Adapter-API projects in order to avoid direct dependencies to new driving adapter 
-   Moved CompositeDrivingAdapter as inner class of JexxaMain because it is only used there   

### Fixed

## \[1.4.2] - 2020-05-22

### Changed
-   Documentation about Jexxa in doc/jexxa.adoc

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
