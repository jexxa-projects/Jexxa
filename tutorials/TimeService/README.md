# TimeService 

## What You Learn

* How to write an application service acting as a so called inbound-port 
* How to declare an outbound-port sending current time  
* How to provide two different implementations, or so called driving adapters, of this outbound-port. One driving adapter uses console output. The other one uses JMS.  

## What you need

*   Understand tutorial `HelloJexxa` 
*   30 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.3 (or higher) installed
*   A running ActiveMQ instance (at least if you start the application with JMS)

## Compile & Start the Application

```console                                                          
mvn clean install
java -jar target/timeservice-jar-with-dependencies.jar 
```
You will see following (or similar) output
```console
[main] INFO io.jexxa.core.JexxaMain - Start BoundedContext 'TimeServiceApplication' with 2 Driving Adapter 
[main] INFO org.eclipse.jetty.util.log - Logging initialized @440ms to org.eclipse.jetty.util.log.Slf4jLog
[main] INFO io.javalin.Javalin - Starting Javalin ...
[main] INFO io.javalin.Javalin - Listening on http://localhost:7000/
[main] INFO io.javalin.Javalin - Javalin started in 129ms \o/
[main] INFO io.jexxa.core.JexxaMain - BoundedContext 'TimeServiceApplication' successfully started in 0.483 seconds
```
