# Change Log
All notable changes to this project will be documented in this file.
 
The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## General note. Since Jexxa 5.0.0 please refer to main [CHANGELOG.md](../CHANGELOG.md)


## \[2.1.7] - 2022-06-21
### Changed
- Updated dependencies

## \[2.1.6] - 2022-05-12
### Changed
- Increment minor number to be in sync with jexxa-core version number 

## \[2.1.5] - 2022-04-26
### Changed
- Minor and patch numbers of jexxa-adapter-api are now kept in sync with other jexxa-core. This should avoid confusions about compatible version numbers

## \[2.1.0] - 2022-02-02
### Added
-   HealthCheck for monitoring driving adapter    

## \[2.0.0] - 2022-01-28
### Changed
-   Replaced `SynchronizationFacade` by `InvocationManager` which enables method invocation by an `InvocationHandler` 

### New
-   Introduced `Interceptor` to improve support of cross-cutting concerns such as logging, or monitoring    

## \[1.0.1] - 2020-10-21
### Fixed
-   `SynchronizationFacade`: Replaced internal `validateNotNull` method by `Objects.requireNonNull`

## \[1.0.0] - 2020-06-01

### Added
-   Initial release
